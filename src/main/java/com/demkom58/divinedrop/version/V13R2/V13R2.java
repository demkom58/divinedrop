package com.demkom58.divinedrop.version.V13R2;

import com.demkom58.divinedrop.version.V13R1.V13NmsHandleNameVersion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V13R2 extends V13NmsHandleNameVersion {
    public V13R2(@NotNull final ResourceClient client) throws Exception {
        super(client,  "v1_13_R2",
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_13_R2.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_13_R2.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_13_R2.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_13_R2.Item"),
                                "getName",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
