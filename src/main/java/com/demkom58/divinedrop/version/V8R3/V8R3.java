package com.demkom58.divinedrop.version.V8R3;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class V8R3 implements Version {
    private final ResourceClient client;
    private final ItemHandler manager;

    public V8R3(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
        this.client = client;
        this.manager = manager;
    }

    @NotNull
    @Override
    public Version.ResourceClient getClient() {
        return client;
    }

    @Override
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if (item == null)
            return null;

        return getName(item);
    }

    @NotNull
    @Override
    public Listener createListener() {
        return new V8Listener(manager);
    }

    private String getName(ItemStack bItemStack) {
        net.minecraft.server.v1_8_R3.ItemStack itemStack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(bItemStack);
        String s = getLangNameNMS(itemStack);

        if (itemStack.getTag() != null && itemStack.getTag().hasKeyOfType("display", 10)) {
            net.minecraft.server.v1_8_R3.NBTTagCompound nbtTagCompound = itemStack.getTag().getCompound("display");

            if (nbtTagCompound.hasKeyOfType("Name", 8))
                s = nbtTagCompound.getString("Name");
        }
        return s;
    }

    private String getLangNameNMS(net.minecraft.server.v1_8_R3.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().e_(itemStack) + ".name").trim();
    }

}
