package com.demkom58.divinedrop.version.V18R1;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.lang.Language;
import com.demkom58.divinedrop.version.V11R1.V11R1;
import com.demkom58.divinedrop.version.V12R1.V12Listener;
import com.demkom58.divinedrop.version.V13R1.V13LangParser;
import com.demkom58.divinedrop.version.Version;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

public class V18R1 implements Version {
    public static final String VERSION = "1.18";
    public static final String PATH = "minecraft/lang/%s.json";

    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemHandler manager;

    private MethodHandle asNMSCopyHandle;
    private MethodHandle getItemHandle;
    private MethodHandle getNameHandle;

    {
        try {
            asNMSCopyHandle = MethodHandles.lookup()
                    .findStatic(
                            Class.forName("org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack"),
                            "asNMSCopy",
                            MethodType.methodType(net.minecraft.world.item.ItemStack.class, ItemStack.class)
                    );
            getItemHandle = MethodHandles.lookup()
                    .findVirtual(
                            Class.forName("net.minecraft.world.item.ItemStack"),
                            "c",
                            MethodType.methodType(net.minecraft.world.item.Item.class)
                    );
            getNameHandle = MethodHandles.lookup()
                    .findVirtual(
                            Class.forName("net.minecraft.world.item.Item"),
                            "a",
                            MethodType.methodType(String.class)
                    );
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private V18R1() {
        this.plugin = null;
        this.data = null;
        this.manager = null;
    }

    public V18R1(@NotNull final DivineDrop plugin,
                 @NotNull final ConfigData data,
                 @NotNull final ItemHandler manager) {
        this.plugin = plugin;
        this.data = data;
        this.manager = manager;
    }

    @Override
    @Nullable
    public String getI18NDisplayName(@Nullable ItemStack item) {
        if (item == null)
            return null;

        return getName(item);
    }

    @NotNull
    @Override
    public String getLangPath(@NotNull final String locale) {
        return String.format(PATH, locale.toLowerCase());
    }

    @NotNull
    @Override
    public Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        return V13LangParser.parseLang(inputStream);
    }

    @NotNull
    @Override
    public String id() {
        return VERSION;
    }

    @NotNull
    @Override
    public Listener createListener() {
        return new V12Listener(manager);
    }

    @NotNull
    @Override
    public String reformatLangCode(@NotNull final String localeCode) {
        return V11R1.langCode(localeCode);
    }

    @NotNull
    @SneakyThrows
    private String getName(ItemStack bItemStack) {
        if (bItemStack.hasItemMeta()) {
            final ItemMeta itemMeta = bItemStack.getItemMeta();
            if (itemMeta.hasDisplayName())
                return itemMeta.getDisplayName();
        }

        return getLangNameNMS((net.minecraft.world.item.ItemStack) asNMSCopyHandle.invokeExact(bItemStack));
    }

    @NotNull
    @SneakyThrows
    private String getLangNameNMS(net.minecraft.world.item.ItemStack itemStack) {
        final net.minecraft.world.item.Item item =
                (net.minecraft.world.item.Item) getItemHandle.bindTo(itemStack).invokeExact();
        final String name = (String) getNameHandle.bindTo(item).invokeExact();
        return Language.getInstance().getLocName(name).trim();
    }
}
