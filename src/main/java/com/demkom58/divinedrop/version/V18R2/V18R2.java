package com.demkom58.divinedrop.version.V18R2;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.version.V13R1.V13NmsHandleNameVersion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V18R2 extends V13NmsHandleNameVersion {
    public V18R2(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) throws Exception {
        super(client, manager,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.world.item.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.ItemStack"),
                                "c",
                                MethodType.methodType(Class.forName("net.minecraft.world.item.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.Item"),
                                "a",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
