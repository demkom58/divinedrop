package com.demkom58.divinedrop.caсhe;

import com.demkom58.divinedrop.lang.Downloader;
import com.demkom58.divinedrop.versions.Version;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CacheStorage {
    private Map<String, Map<String, String>> versionLangsCache = new HashMap<>();

    private CacheStorage() { }

    protected CacheStorage(Map<String, Map<String, String>> versionLangsCache) {
        this.versionLangsCache = versionLangsCache;
    }

    public static CacheStorage load() {
        try (JsonReader reader = new JsonReader(new InputStreamReader(CacheStorage.class.getResourceAsStream("/cache.json")))) {
            return Downloader.GSON.fromJson(reader, CacheStorage.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public @Nullable String getLink(@NotNull final Version version,
                                    @NotNull final String language) {
        final Map<String, String> languages = versionLangsCache.get(version.id());

        if (languages == null)
            return null;

        return languages.get(version.reformatLangCode(language));
    }

}
