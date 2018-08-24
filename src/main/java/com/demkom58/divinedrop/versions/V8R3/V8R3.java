package com.demkom58.divinedrop.versions.V8R3;

import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.versions.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class V8R3 implements Version {
    public static final String VERSION = "1.8.8";
    public static final String PATH = "minecraft/lang/%s.lang";

    @Override
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if(item == null) return null;
        return getName(item);
    }

    @NotNull
    @Override
    public String getPath() {
        return PATH;
    }

    @NotNull
    @Override
    public Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        return V8LangParser.parseLang(inputStream);
    }

    @NotNull
    @Override
    public String getVersion() {
        return VERSION;
    }

    @NotNull
    @Override
    public Listener getListener() {
        return new V8Listener();
    }

    private String getName(ItemStack bItemStack) {
        net.minecraft.server.v1_8_R3.ItemStack itemStack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(bItemStack);
        String s = getLangNameNMS(itemStack);
        if (itemStack.getTag() != null && itemStack.getTag().hasKeyOfType("display", 10)) {
            net.minecraft.server.v1_8_R3.NBTTagCompound nbttagcompound = itemStack.getTag().getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                s = nbttagcompound.getString("Name");
            }
        }
        return s;
    }

    private String getLangNameNMS(net.minecraft.server.v1_8_R3.ItemStack itemStack) {
        return Language.getInstance().getLocName(getNameNMS(itemStack) + ".name").trim();
    }

    private String getNameNMS(net.minecraft.server.v1_8_R3.ItemStack itemStack) {
        String name = itemStack.getItem().e_(itemStack);
        return name == null ? "" : Language.getInstance().getLocName(name);
    }
}
