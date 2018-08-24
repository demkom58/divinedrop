package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.Data;
import com.demkom58.divinedrop.versions.VersionUtil;
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

    private final static String VERSIONS_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private final static String ASSETS_URL = "http://resources.download.minecraft.net/";
    private final Gson gson = new Gson();

    public void downloadResource(String locale, File destination) throws IOException {
        final VersionManifest vm = this.downloadObject(new URL(Downloader.VERSIONS_LIST), VersionManifest.class);
        final ClientVersion client = downloadObject(new URL(vm.getLatestRelease().getUrl()), ClientVersion.class);
        final AssetIndex ai = downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);

        final String hash = ai.getLocaleHash(locale);
        Bukkit.getServer().getConsoleSender().sendMessage("§eDownloading §6{0}.data §efile (hash: §6{1}§e)"
                .replace("{0}", locale).replace("{1}", hash));

        final String assetPath = Downloader.ASSETS_URL + this.createPathFromHash(hash);
        FileUtils.copyURLToFile(new URL(assetPath), destination, 100, 500);
    }

    private <T> T downloadObject(URL url, Class<T> object) throws IOException {
        try (InputStream input = url.openConnection().getInputStream()) {

            final InputStreamReader reader = new InputStreamReader(input);
            final JsonReader jsonReader = new JsonReader(reader);

            final T prepared = gson.fromJson(jsonReader, object);

            jsonReader.close();
            reader.close();

            return prepared;
        }
    }

    private String createPathFromHash(String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }

    class VersionManifest {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> latest;
        private ArrayList<RemoteClient> versions;

        @NotNull
        public RemoteClient getLatestRelease() {
            final String version = VersionUtil.getVersion().getVersion();

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
            final LinkedTreeMap<String, String> asset = objects.get(String.format(VersionUtil.getVersion().getPath(), locale.toLowerCase()));
                if (asset == null) {
                    Data.lang = "en_CA";
                    Data.langManager.downloadLang(Data.lang, VersionUtil.getVersion());
                    return "";
                }

            final String hash = asset.get("hash");
            return hash != null ? hash : "";
        }
    }

}