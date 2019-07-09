package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.versions.Version;
import com.google.common.collect.Maps;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.Map;

public class Language {
    private static Language instance;
    private final Map<String, String> langMap = Maps.newHashMap();
    private long currentTimeMillis = 0;

    public Language() {
        instance = this;
    }

    public static Language getInstance() {
        return instance;
    }

    public void updateLangMap(@NotNull final Version version,
                              @NotNull final String langPath) {
        try {
            final InputStream inputStream = new FileInputStream(langPath);

            final InputStreamReader reader = new InputStreamReader(inputStream);
            final JsonReader jsonReader = new JsonReader(reader);

            langMap.clear();
            mergeMap(version.parseLang(inputStream), langMap);

            jsonReader.close();
            reader.close();
        } catch (IOException ignored) {
        } finally {
            currentTimeMillis = System.currentTimeMillis();
        }
    }

    private <K, V> void mergeMap(@NotNull Map<K, V> from, @NotNull Map<K, V> to) {
        for (Map.Entry<K, V> entry : from.entrySet())
            to.put(entry.getKey(), entry.getValue());
    }

    public synchronized String getName(String path, Object... objects) {
        String name = this.getLangName(path);
        try {
            return String.format(name, objects);
        } catch (IllegalFormatException ex) {
            return "Format error: " + name;
        }
    }

    @NotNull
    private synchronized String getLangName(@NotNull String path) {
        final String result = langMap.get(path);
        return result == null
                ? path
                : result;
    }

    @NotNull
    public String getLocName(@NotNull String path) {
        return getLangName(path);
    }

    public String getLocName(String path, Object... objects) {
        return getName(path, objects);
    }

    public synchronized boolean contains(String path) {
        return langMap.containsKey(path);
    }

    public long getCurrentMillis() {
        return currentTimeMillis;
    }

}
