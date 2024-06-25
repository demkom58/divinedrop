package com.demkom58.divinedrop.cache;

import com.demkom58.divinedrop.lang.Downloader;
import com.demkom58.divinedrop.version.SupportedVersion;
import com.demkom58.divinedrop.version.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.demkom58.divinedrop.lang.Downloader.*;

public class CacheGenerator {
    private static final Logger LOGGER = Logger.getLogger("CacheGenerator");
    private static final File ROOT_DIRECTORY = new File(System.getProperty("user.dir"));

    public static void main(String[] args) throws Exception {
        LOGGER.setUseParentHandlers(false);

        final SupportedVersion[] versions = SupportedVersion.values();
        final Map<String, Map<String, String>> versionLangs = new HashMap<>();

        for (SupportedVersion supportedVersion : versions) {
            final Version.ResourceClient versionClient = supportedVersion.getClient();
            final String versionId = versionClient.id();

            LOGGER.info("Generating cache links for version " + supportedVersion.name());

            final Map<String, String> langsMap = new HashMap<>();
            versionLangs.put(versionId, langsMap);

            final VersionManifest vm = downloadObject(new URL(Downloader.VERSIONS_LIST), VersionManifest.class);
            final ClientVersion client = downloadObject(new URL(vm.getRelease(versionId).getUrl()), ClientVersion.class);
            final AssetIndex ai = downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);

            final List<String> locales = ai.getObjects().keySet().stream()
                    .filter(str -> str.contains("lang"))
                    .map(str -> str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf(".")))
                    .collect(Collectors.toList());

            for (String locale : locales) {
                try {
                    final String hash = ai.getLocaleHash(versionClient.getLangPath(locale));
                    final String langAssetPath = Downloader.ASSETS_URL + createPathFromHash(hash);

                    langsMap.put(locale, langAssetPath);
                    LOGGER.info("DONE > " + locale);
                } catch (IllegalArgumentException e) {
                    LOGGER.severe("An error occurred while getting hash of language " + locale + " on " + versionId);
                }
            }
        }

        final File cacheFile = new File(ROOT_DIRECTORY, "cache.json");

        LOGGER.info("Writing cache to " + cacheFile.getName() + "...");
        try (final FileOutputStream fos = new FileOutputStream(cacheFile)) {
            fos.write(GSON.toJson(new CacheStorage(versionLangs)).getBytes());
        }

        LOGGER.info("All data saved!");
    }

}
