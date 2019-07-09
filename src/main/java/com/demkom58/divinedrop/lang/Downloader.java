package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.versions.VersionManager;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Downloader {

    private static final String VERSIONS_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final String ASSETS_URL = "http://resources.download.minecraft.net/";
    private static final Gson GSON = new Gson();

    private final VersionManager versionManager;
    private final ConfigurationData data;
    private final LangManager langManager;

    public Downloader(@NotNull final VersionManager versionManager,
                      @NotNull final ConfigurationData data,
                      @NotNull final LangManager langManager) {
        this.versionManager = versionManager;
        this.data = data;
        this.langManager = langManager;
    }

    public void downloadResource(@NotNull String locale, @NotNull File destination) throws IOException {
        final VersionManifest vm = this.downloadObject(new URL(Downloader.VERSIONS_LIST), VersionManifest.class);
        final ClientVersion client = downloadObject(new URL(vm.getRelease().getUrl()), ClientVersion.class);
        final AssetIndex ai = downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);

        final String hash = ai.getLocaleHash(locale);
        Bukkit.getServer().getConsoleSender().sendMessage("§eDownloading §6{0}§e file (hash: §6{1}§e)"
                .replace("{0}", locale).replace("{1}", hash));

        final String assetPath = Downloader.ASSETS_URL + this.createPathFromHash(hash);
        FileUtils.copyURLToFile(new URL(assetPath), destination, 100, 500);
    }

    @NotNull
    private <T> T downloadObject(@NotNull URL url, @NotNull Class<T> object) throws IOException {
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
    private String createPathFromHash(@NotNull String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }

    class VersionManifest {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> latest;
        private ArrayList<RemoteClient> versions;

        @NotNull
        public RemoteClient getRelease() {
            final String version = versionManager.getVersion().name();

            for (RemoteClient c : this.versions)
                if (c.getId().equals(version)) return c;

            throw new IllegalArgumentException(version + " does not exists. There something is definitely wrong.");
        }

    }

    class RemoteClient {
        private String id, url;

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    }

    class ClientVersion {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> assetIndex;

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

    class AssetIndex {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, LinkedTreeMap<String, String>> objects;

        @NotNull
        public String getLocaleHash(@NotNull final String locale) {
            final LinkedTreeMap<String, String> asset = objects.get(versionManager.getVersion().getLangPath(locale));
            if (asset == null) {
                data.lang = "en_CA";
                langManager.downloadLang(data.lang, versionManager.getVersion());
                return "";
            }

            final String hash = asset.get("hash");

            return hash != null
                    ? hash
                    : "";
        }
    }

}