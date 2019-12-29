package com.demkom58.divinedrop.version.V15R1;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.drop.ItemHandler;
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

public class V15R1 implements Version {
    public static final String VERSION = "1.15";
    public static final String PATH = "minecraft/lang/%s.json";

    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemHandler manager;

    private V15R1() {
        this.plugin = null;
        this.data = null;
        this.manager = null;
    }

    public V15R1(@NotNull final DivineDrop plugin,
                 @NotNull final ConfigData data,
                 @NotNull final ItemHandler manager) {
        this.plugin = plugin;
        this.data = data;
        this.manager = manager;
    }

    @Override
    @Nullable
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
    public Listener createListener() {
        return new V12Listener(manager);
    }

    @NotNull
    @Override
    public String reformatLangCode(@NotNull final String localeCode) {
        return V11R1.langCode(localeCode);
    }

    @Nullable
    private String getName(ItemStack bItemStack) {
        final net.minecraft.server.v1_15_R1.ItemStack itemStack = org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack.asNMSCopy(bItemStack);
        final net.minecraft.server.v1_15_R1.NBTTagCompound nbtTagCompound = itemStack.b("display");

        if (nbtTagCompound != null) {
            if (nbtTagCompound.hasKeyOfType("Name", 8)) {
                final net.minecraft.server.v1_15_R1.IChatBaseComponent name =
                        net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer.a(nbtTagCompound.getString("Name"));
                return name == null ? null : name.getString();
            }
        }

        return getLangNameNMS(itemStack);
    }

    private String getLangNameNMS(net.minecraft.server.v1_15_R1.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().getName()).trim();
    }

}
