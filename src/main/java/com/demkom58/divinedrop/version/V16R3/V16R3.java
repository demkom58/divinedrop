package com.demkom58.divinedrop.version.V16R3;

import com.demkom58.divinedrop.version.V13R1.V13NmsHandleNameVersion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V16R3 extends V13NmsHandleNameVersion {
    public V16R3(@NotNull final ResourceClient client) throws Exception {
        super(client,  "v1_16_R3",
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_16_R3.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_16_R3.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_16_R3.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_16_R3.Item"),
                                "getName",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
