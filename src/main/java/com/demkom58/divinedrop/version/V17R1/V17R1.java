package com.demkom58.divinedrop.version.V17R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.version.V13R1.V13NmsHandleNameVersion;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V17R1 extends V13NmsHandleNameVersion {
    @SneakyThrows
    public V17R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
        super(client, manager,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(net.minecraft.world.item.ItemStack.class, ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.ItemStack"),
                                "getItem",
                                MethodType.methodType(net.minecraft.world.item.Item.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.Item"),
                                "getName",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
