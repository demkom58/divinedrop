package com.demkom58.divinedrop.version.V8R3;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.Version;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;

public abstract class NmsHandleNameVersion implements Version {
    protected final ResourceClient client;
    protected final ItemHandler manager;

    protected MethodHandle asNMSCopyHandle;
    protected MethodHandle getItemHandle;
    protected MethodHandle getNameHandle;

    public NmsHandleNameVersion(@NotNull final ResourceClient client,
                                   @NotNull final ItemHandler manager,
                                   @NotNull final MethodHandle asNMSCopyHandle,
                                   @NotNull final MethodHandle getItemHandle,
                                   @NotNull final MethodHandle getNameHandle) {
        this.client = client;
        this.manager = manager;
        this.asNMSCopyHandle = asNMSCopyHandle;
        this.getItemHandle = getItemHandle;
        this.getNameHandle = getNameHandle;
    }

    @NotNull
    @Override
    public Version.ResourceClient getClient() {
        return client;
    }

    @Override
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if (item == null)
            return null;

        return getName(item);
    }

    @NotNull
    @Override
    public abstract Listener createListener();

    @NotNull
    @SneakyThrows
    protected String getName(ItemStack bItemStack) {
        if (bItemStack.hasItemMeta()) {
            final ItemMeta itemMeta = bItemStack.getItemMeta();
            if (itemMeta.hasDisplayName())
                return itemMeta.getDisplayName();
        }

        Object itemStack = asNMSCopyHandle.invokeExact(bItemStack);
        return getLangNameNMS(itemStack);
    }

    @NotNull
    @SneakyThrows
    protected String getLangNameNMS(Object itemStack) {
        final Object item = getItemHandle.bindTo(itemStack).invokeExact();
        final String name = (String) getNameHandle.bindTo(item).invokeExact(itemStack);
        return Language.getInstance().getLocName(name + ".name").trim();
    }

}
