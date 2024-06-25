package com.demkom58.divinedrop.version.V13R1;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V13R1 extends V13NmsHandleNameVersion {
    public V13R1(@NotNull final ResourceClient client) throws Exception {
        super(client,  "v1_13_R1",
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_13_R1.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_13_R1.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_13_R1.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_13_R1.Item"),
                                "getName",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
