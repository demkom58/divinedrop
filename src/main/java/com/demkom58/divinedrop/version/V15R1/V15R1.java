package com.demkom58.divinedrop.version.V15R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.V12R1.V12Listener;
import com.demkom58.divinedrop.version.Version;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class V15R1 implements Version {
    private final ResourceClient client;
    private final ItemHandler manager;

    public V15R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
        this.client = client;
        this.manager = manager;
    }

    @NotNull
    @Override
    public Version.ResourceClient getClient() {
        return client;
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
    public Listener createListener() {
        return new V12Listener(manager);
    }

    @NotNull
    private String getName(ItemStack bItemStack) {
        if (bItemStack.hasItemMeta()) {
            final ItemMeta itemMeta = bItemStack.getItemMeta();
            if (itemMeta.hasDisplayName())
                return itemMeta.getDisplayName();
        }

        return getLangNameNMS(org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack.asNMSCopy(bItemStack));
    }

    @NotNull
    private String getLangNameNMS(net.minecraft.server.v1_15_R1.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().getName()).trim();
    }

}
