package com.demkom58.divinedrop.version.V8R3;

import com.demkom58.divinedrop.version.SimpleResourceClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class V8ResourceClient extends SimpleResourceClient {
    public static final String PATH = "minecraft/lang/%s.lang";

    public V8ResourceClient(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull String getLangPath(@NotNull String locale) {
        return String.format(PATH, langFormat(locale));
    }

    @Override
    public @NotNull String reformatLangCode(@NotNull String localeCode) {
        return langCode(localeCode);
    }

    @Override
    public @NotNull Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        return V8LangParser.parseLang(inputStream);
    }

    public static String langFormat(@NotNull final String locale) {
        final String[] lang = locale.split("_");

        if (lang.length == 1)
            return lang[0];

        return lang[0] + "_" + lang[1].toUpperCase();
    }

    public static @NotNull String langCode(@NotNull final String localeCode) {
        String[] parts = localeCode.split("_", 2);
        return parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
    }
}
