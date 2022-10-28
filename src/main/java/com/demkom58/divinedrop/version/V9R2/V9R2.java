package com.demkom58.divinedrop.version.V9R2;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.version.V8R3.V8NmsHandleNameVersion;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V9R2 extends V8NmsHandleNameVersion {
    @SneakyThrows
    public V9R2(@NotNull final ResourceClient client, @NotNull final ItemHandler manager) {
        super(client, manager,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_9_R2.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_9_R2.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.server.v1_9_R2.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.server.v1_9_R2.Item"),
                                "f_",
                                MethodType.methodType(String.class, Class.forName("net.minecraft.server.v1_9_R2.ItemStack.class"))
                        )
        );
    }
}
