package com.demkom58.divinedrop.version;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Version {

    @NotNull ResourceClient getClient();

    @Nullable String getI18NDisplayName(@Nullable final ItemStack item);

    @NotNull Listener createListener();

    interface ResourceClient {
        @NotNull String id();

        @NotNull String getLangPath(@NotNull final String locale);

        @NotNull String reformatLangCode(@NotNull final String localeCode);

        @NotNull Map<String, String> parseLang(@NotNull final InputStream inputStream) throws IOException;
    }

}
