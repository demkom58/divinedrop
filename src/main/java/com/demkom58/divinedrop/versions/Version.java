package com.demkom58.divinedrop.versions;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Version {

    String getI18NDisplayName(@Nullable final ItemStack item);
    @NotNull String getLangPath(@NotNull final String locale);
    @NotNull Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException;
    @NotNull String name();
    @NotNull Listener getListener();

}
