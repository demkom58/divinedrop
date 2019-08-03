package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.versions.Version;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Downloader {

    public static final String VERSIONS_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    public static final String ASSETS_URL = "http://resources.download.minecraft.net/";
    public static final Gson GSON = new Gson();

    private final ConfigData data;
    private final LangManager langManager;

    public Downloader(@NotNull final ConfigData data,
                      @NotNull final LangManager langManager) {
        this.data = data;
        this.langManager = langManager;
    }

    public void downloadResource(@NotNull final Version version,
                                 @NotNull final String locale,
                                 @NotNull final File destination) throws IOException {
        final String versionId = version.id();

        final VersionManifest vm = downloadObject(new URL(Downloader.VERSIONS_LIST), VersionManifest.class);
        final ClientVersion client = downloadObject(new URL(vm.getRelease(versionId).getUrl()), ClientVersion.class);
        final AssetIndex ai;

        try {
            ai = downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(e.getMessage());
            data.setLang("en_CA");
            langManager.manageLang(data.getLang(), version);
            return;
        }

        final String hash = ai.getLocaleHash(version.getLangPath(locale));
        Bukkit.getServer().getConsoleSender().sendMessage("§eDownloading §6{0}§e file (hash: §6{1}§e)"
                .replace("{0}", locale).replace("{1}", hash));

        final String assetPath = Downloader.ASSETS_URL + createPathFromHash(hash);
        copyURLToFile(new URL(assetPath), destination, 100, 500);
    }

    @NotNull
    public static <T> T downloadObject(@NotNull URL url, @NotNull Class<T> object) throws IOException {
        try (InputStream input = url.openConnection().getInputStream()) {
            final InputStreamReader reader = new InputStreamReader(input);
            final JsonReader jsonReader = new JsonReader(reader);

            final T prepared = GSON.fromJson(jsonReader, object);

            jsonReader.close();
            reader.close();

            return prepared;
        }
    }

    @NotNull
    public static String createPathFromHash(@NotNull String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }

    private static void copyURLToFile(@NotNull final URL url,
                                      @NotNull final File destination,
                                      final int connectTimeout,
                                      final int readTimeout) throws IOException {
        final URLConnection conn = url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        try (final InputStream in = conn.getInputStream()) {
            try (FileOutputStream out = new FileOutputStream(destination)) {
                final byte[] buffer = new byte[4096];

                int l;
                while (-1 != (l = in.read(buffer))) {
                    out.write(buffer, 0, l);
                }
            }
        }
    }

    public static class VersionManifest {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Getter private LinkedTreeMap<String, String> latest;
        @Getter private ArrayList<RemoteClient> versions;

        @NotNull
        public RemoteClient getRelease(String id) {
            for (RemoteClient c : this.versions)
                if (id.equals(c.getId()))
                    return c;

            throw new IllegalArgumentException(id + " does not exists. There something is definitely wrong.");
        }

    }

    @Getter
    public static class RemoteClient {
        private String id, url;
    }

    public static class ClientVersion {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Getter private LinkedTreeMap<String, String> assetIndex;

        public String getId() {
            return this.assetIndex.get("id");
        }

        public String getSHA1() {
            return this.assetIndex.get("sha1");
        }

        public String getSize() {
            return this.assetIndex.get("size");
        }

        public String getAssetUrl() {
            return this.assetIndex.get("url");
        }

        public String getTotalSize() {
            return this.assetIndex.get("totalSize");
        }

    }

    public static class AssetIndex {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Getter private LinkedTreeMap<String, LinkedTreeMap<String, String>> objects;

        @NotNull
        public String getLocaleHash(@NotNull final String langPath) throws IllegalArgumentException {
            final LinkedTreeMap<String, String> asset = objects.get(langPath);

            if (asset == null)
                throw new IllegalArgumentException("Unable to get lang hash.");

            final String hash = asset.get("hash");

            return hash != null
                    ? hash
                    : "";
        }
    }

}