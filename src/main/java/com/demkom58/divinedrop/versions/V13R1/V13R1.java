package com.demkom58.divinedrop.versions.V13R1;

import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.versions.V12R1.V12Listener;
import com.demkom58.divinedrop.versions.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class V13R1 implements Version {
    public static final String VERSION = "1.13";
    public static final String PATH = "minecraft/lang/%s.json";

    @Override
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if(item == null) return null;
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
    public String name() {
        return VERSION;
    }

    @NotNull
    @Override
    public Listener getListener() {
        return new V12Listener();
    }

    private String getName(ItemStack bItemStack) {
        final net.minecraft.server.v1_13_R1.ItemStack itemStack = org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asNMSCopy(bItemStack);
        final net.minecraft.server.v1_13_R1.NBTTagCompound nbtTagCompound = itemStack.b("display");

        if (nbtTagCompound != null) {
            if (nbtTagCompound.hasKeyOfType("Name", 8))
                return nbtTagCompound.getString("Name");
            if (nbtTagCompound.hasKeyOfType("LocName", 8))
                return Language.getInstance().getLocName(nbtTagCompound.getString("LocName"));
        }

        return Language.getInstance().getLocName(itemStack.getItem().getName()).trim();
    }

}
