package com.demkom58.divinedrop.version.V13R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.V12R1.V12Listener;
import com.demkom58.divinedrop.version.V8R3.V8NmsHandleNameVersion;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

public class V13NmsHandleNameVersion extends V8NmsHandleNameVersion {
    public V13NmsHandleNameVersion(@NotNull final ResourceClient client,
                                   @NotNull final ItemHandler manager,
                                   @NotNull final MethodHandle asNMSCopyHandle,
                                   @NotNull final MethodHandle getItemHandle,
                                   @NotNull final MethodHandle getNameHandle) {
        super(client, manager, asNMSCopyHandle, getItemHandle, getNameHandle);
    }

    @NotNull
    @Override
    public Listener createListener() {
        return new V12Listener(manager);
    }

    @NotNull
    @SneakyThrows
    @Override
    protected String getLangNameNMS(Object itemStack) {
        final Object item = getItemHandle.bindTo(itemStack).invokeExact();
        final String name = (String) getNameHandle.bindTo(item).invokeExact();
        return Language.getInstance().getLocName(name).trim();
    }
}
