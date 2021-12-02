package com.demkom58.divinedrop.version;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class SimpleResourceClient implements Version.ResourceClient {
    private final String id;

    protected SimpleResourceClient(@NotNull final String id) {
        this.id = id;
    }

    @Override
    public @NotNull String id() {
        return id;
    }

    @Override
    public abstract @NotNull String getLangPath(@NotNull final String locale);

    @Override
    public abstract @NotNull String reformatLangCode(@NotNull final String localeCode);

    @Override
    public abstract @NotNull Map<String, String> parseLang(@NotNull final InputStream inputStream) throws IOException;
}
