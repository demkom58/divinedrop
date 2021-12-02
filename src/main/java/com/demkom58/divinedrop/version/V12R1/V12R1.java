package com.demkom58.divinedrop.version.V12R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class V12R1 implements Version {
    private final ResourceClient client;
    private final ItemHandler manager;

    public V12R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
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
        return new V12Listener(manager);
    }


    private String getName(ItemStack bItemStack) {
        net.minecraft.server.v1_12_R1.ItemStack itemStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(bItemStack);
        net.minecraft.server.v1_12_R1.NBTTagCompound nbtTagCompound = itemStack.d("display");

        if (nbtTagCompound != null) {
            if (nbtTagCompound.hasKeyOfType("Name", 8))
                return nbtTagCompound.getString("Name");

            if (nbtTagCompound.hasKeyOfType("LocName", 8))
                return Language.getInstance().getLocName(nbtTagCompound.getString("LocName"));
        }

        return getLangNameNMS(itemStack);
    }

    private String getLangNameNMS(net.minecraft.server.v1_12_R1.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().a(itemStack) + ".name").trim();
    }

}
