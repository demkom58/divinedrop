package com.demkom58.divinedrop.version.V11R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.version.V8R3.V8NmsHandleNameVersion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V11R1 extends V8NmsHandleNameVersion {
    public V11R1(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) throws Exception {
        super(client, manager,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_11_R1.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_11_R1.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_11_R1.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_11_R1.Item"),
                                "a",
                                MethodType.methodType(String.class, Class.forName("net.minecraft.server.v1_11_R1.ItemStack.class"))
                        )
        );
    }
}
