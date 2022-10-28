package com.demkom58.divinedrop.version.V8R3;

import com.demkom58.divinedrop.drop.ItemHandler;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V8R3 extends V8NmsHandleNameVersion {
    @SneakyThrows
    public V8R3(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
        super(client, manager,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_8_R3.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_8_R3.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_8_R3.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_8_R3.Item"),
                                "e_",
                                MethodType.methodType(String.class, Class.forName("net.minecraft.server.v1_8_R3.ItemStack.class"))
                        )
        );
    }
}
