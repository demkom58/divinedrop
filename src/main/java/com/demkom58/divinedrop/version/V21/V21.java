package com.demkom58.divinedrop.version.V21;

import com.demkom58.divinedrop.version.V13R1.V13NmsHandleNameVersion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class V21 extends V13NmsHandleNameVersion {
    public V21(@NotNull final ResourceClient client) throws Exception {
        super(client,  null,
                MethodHandles.lookup()
                        .findStatic(
                                Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack"),
                                "asNMSCopy",
                                MethodType.methodType(Class.forName("net.minecraft.world.item.ItemStack"), ItemStack.class)
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.ItemStack"),
                                "getItem",
                                MethodType.methodType(Class.forName("net.minecraft.world.item.Item"))
                        ),
                MethodHandles.lookup()
                        .findVirtual(
                                Class.forName("net.minecraft.world.item.Item"),
                                "getDescriptionId",
                                MethodType.methodType(String.class)
                        )
        );
    }
}
