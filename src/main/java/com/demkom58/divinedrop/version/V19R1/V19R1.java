package com.demkom58.divinedrop.version.V19R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.V12R1.V12Listener;
import com.demkom58.divinedrop.version.Version;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V19R1 implements Version {
    private final ResourceClient client;
    private final ItemHandler manager;

    private MethodHandle asNMSCopyHandle;
    private MethodHandle getItemHandle;
    private MethodHandle getNameHandle;

    {
        try {
            asNMSCopyHandle = MethodHandles.lookup()
                    .findStatic(
                            Class.forName("org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack"),
                            "asNMSCopy",
                            MethodType.methodType(net.minecraft.world.item.ItemStack.class, ItemStack.class)
                    );
            getItemHandle = MethodHandles.lookup()
                    .findVirtual(
                            Class.forName("net.minecraft.world.item.ItemStack"),
                            "c",
                            MethodType.methodType(net.minecraft.world.item.Item.class)
                    );
            getNameHandle = MethodHandles.lookup()
                    .findVirtual(
                            Class.forName("net.minecraft.world.item.Item"),
                            "a",
                            MethodType.methodType(String.class)
                    );
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public V19R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
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
    @SneakyThrows
    private String getName(ItemStack bItemStack) {
        if (bItemStack.hasItemMeta()) {
            final ItemMeta itemMeta = bItemStack.getItemMeta();
            if (itemMeta.hasDisplayName())
                return itemMeta.getDisplayName();
        }

        return getLangNameNMS((net.minecraft.world.item.ItemStack) asNMSCopyHandle.invokeExact(bItemStack));
    }

    @NotNull
    @SneakyThrows
    private String getLangNameNMS(net.minecraft.world.item.ItemStack itemStack) {
        final net.minecraft.world.item.Item item =
                (net.minecraft.world.item.Item) getItemHandle.bindTo(itemStack).invokeExact();
        final String name = (String) getNameHandle.bindTo(item).invokeExact();
        return Language.getInstance().getLocName(name).trim();
    }
}
