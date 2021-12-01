package com.demkom58.divinedrop.version.V17R1;

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

public class V17R1 implements Version {
    public static final String VERSION = "1.17";
    public static final String PATH = "minecraft/lang/%s.json";

    private final ResourceClient client;
    private final ItemHandler manager;

    private MethodHandle methodHandle;

    {
        try {
            methodHandle = MethodHandles.lookup()
                    .findStatic(
                            Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack"),
                            "asNMSCopy",
                            MethodType.methodType(net.minecraft.world.item.ItemStack.class, ItemStack.class)
                    );
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public V17R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
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

        return getLangNameNMS((net.minecraft.world.item.ItemStack) methodHandle.invokeExact(bItemStack));
    }

    @NotNull
    private String getLangNameNMS(net.minecraft.world.item.ItemStack itemStack) {
        return Language.getInstance().getLocName(itemStack.getItem().getName()).trim();
    }
}