package com.demkom58.divinedrop.version.V8R3;

import com.demkom58.divinedrop.drop.ItemHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class V8NmsHandleNameVersion extends NmsHandleNameVersion {
    public V8NmsHandleNameVersion(@NotNull ResourceClient client,
                                  @NotNull ItemHandler manager,
                                  @NotNull MethodHandle asNMSCopyHandle,
                                  @NotNull MethodHandle getItemHandle,
                                  @NotNull MethodHandle getNameHandle) {
        super(
                client,
                manager,
                asNMSCopyHandle.asType(MethodType.methodType(Object.class, ItemStack.class)),
                getItemHandle.asType(MethodType.methodType(Object.class, Object.class)),
                getNameHandle.asType(MethodType.methodType(String.class, Object.class, Object.class))
        );
    }

    @NotNull
    @Override
    public Listener createListener() {
        return new V8Listener(manager);
    }
}
