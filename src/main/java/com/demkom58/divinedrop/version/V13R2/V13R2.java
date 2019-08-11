package com.demkom58.divinedrop.version.V13R2;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.V11R1.V11R1;
import com.demkom58.divinedrop.version.V12R1.V12Listener;
import com.demkom58.divinedrop.version.V13R1.V13LangParser;
import com.demkom58.divinedrop.version.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class V13R2 implements Version {
    public static final String VERSION = "1.13.1";
    public static final String PATH = "minecraft/lang/%s.json";

    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemsHandler logic;

    private V13R2() {
        this.plugin = null;
        this.data = null;
        this.logic = null;
    }

    public V13R2(@NotNull final DivineDrop plugin,
                 @NotNull final ConfigData data,
                 @NotNull final ItemsHandler logic) {
        this.plugin = plugin;
        this.data = data;
        this.logic = logic;
    }

    @Override
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if (item == null)
            return null;

        return getName(item);
    }

    @NotNull
    @Override
    public String getLangPath(@NotNull final String locale) {
        return String.format(PATH, locale.toLowerCase());
    }

    @NotNull
    @Override
    public Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        return V13LangParser.parseLang(inputStream);
    }

    @NotNull
    @Override
    public String id() {
        return VERSION;
    }

    @NotNull
    @Override
    public Listener getListener() {
        return new V12Listener(plugin, data, logic);
    }

    @Override
    public @NotNull String reformatLangCode(@NotNull final String localeCode) {
        return V11R1.langCode(localeCode);
    }

    private String getName(ItemStack bItemStack) {
        final net.minecraft.server.v1_13_R2.ItemStack itemStack = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(bItemStack);
        final net.minecraft.server.v1_13_R2.NBTTagCompound nbtTagCompound = itemStack.b("display");

        if (nbtTagCompound != null) {
            if (nbtTagCompound.hasKeyOfType("Name", 8)) {
                final net.minecraft.server.v1_13_R2.IChatBaseComponent name =
                        net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a(nbtTagCompound.getString("Name"));
                return name == null ? null : name.getString();
            }
        }

        return getLangNameNMS(itemStack);
    }

    private String getLangNameNMS(net.minecraft.server.v1_13_R2.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().getName()).trim();
    }

}
