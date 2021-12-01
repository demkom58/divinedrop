package com.demkom58.divinedrop.version.V13R1;

import com.demkom58.divinedrop.version.V11R1.V11ResourceClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class V13ResourceClient extends V11ResourceClient {
    public static final String PATH = "minecraft/lang/%s.json";

    public V13ResourceClient(@NotNull String id) {
        super(id);
    }

    @NotNull
    @Override
    public String getLangPath(@NotNull final String locale) {
        return String.format(PATH, locale.toLowerCase());
    }

    @Override
    public @NotNull Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        return V13LangParser.parseLang(inputStream);
    }
}
