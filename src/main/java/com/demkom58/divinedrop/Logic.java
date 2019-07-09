package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Logic {
    public static final Set<Item> ITEMS_LIST = Collections.newSetFromMap(new WeakHashMap<>());

    private final DivineDrop plugin;
    private final VersionManager versionManager;
    private final ConfigurationData data;

    public Logic(@NotNull final DivineDrop plugin,
                 @NotNull final VersionManager versionManager,
                 @NotNull final ConfigurationData data) {
        this.plugin = plugin;
        this.versionManager = versionManager;
        this.data = data;
    }

    public void registerItems(@NotNull final Entity[] entities) {
        Arrays.stream(entities)
                .filter(entity -> entity instanceof Item)
                .forEach(entity -> ITEMS_LIST.add((Item) entity));
    }

    public void registerDeathDrop(@NotNull final PlayerDeathEvent event) {
        data.deathDroppedItemsList.addAll(event.getDrops());
    }

    public void removeTimers() {
        ITEMS_LIST.forEach(item -> item.setCustomName(getLiteCustomName(item)));
    }

    public String getLiteCustomName(@NotNull final Item item) {
        return data.liteFormat
                .replace(ConfigurationData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(ConfigurationData.NAME_PLACEHOLDER, getDisplayName(item));
    }

    public String getDisplayName(@NotNull final Item item) {
        return versionManager.getVersion().getI18NDisplayName(item.getItemStack());
    }

    public void removeTimer(@NotNull final Item item,
                            @NotNull final DataContainer dataContainer) {
        String format = dataContainer.getFormat();

        if (format == null)
            format = data.liteFormat;

        if (format.equals(data.format))
            format = data.liteFormat;

        item.setCustomName(format
                .replace(ConfigurationData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(ConfigurationData.NAME_PLACEHOLDER, getDisplayName(item))
        );
        ITEMS_LIST.remove(item);
    }

    public void setTimer(@NotNull final Item item,
                         @NotNull final DataContainer dataContainer) {
        if (dataContainer.getTimer() <= 0) {
            item.remove();
            ITEMS_LIST.remove(item);
        }

        if (dataContainer.getFormat() == null)
            dataContainer.setFormat("");

        item.setMetadata(ConfigurationData.METADATA_COUNTDOWN, new FixedMetadataValue(plugin, dataContainer));
        item.setCustomName(dataContainer.getFormat()
                .replace(ConfigurationData.TIMER_PLACEHOLDER, String.valueOf(dataContainer.getTimer()))
                .replace(ConfigurationData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(ConfigurationData.NAME_PLACEHOLDER, getDisplayName(item))
        );
    }

    public void registerCountdown() {
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, () ->
                ITEMS_LIST.forEach(item -> Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> handleItem(item))),
                0L,
                20L
        );
    }

    private void handleItem(@NotNull final Item item) {
        final List<MetadataValue> metadataCountdowns = item.getMetadata(ConfigurationData.METADATA_COUNTDOWN);
        if (metadataCountdowns.isEmpty()) {

            int timer = data.timerValue;
            String format = data.format;
            Material material = item.getItemStack().getType();
            String name = item.getItemStack().getItemMeta().getDisplayName();

            if (name == null)
                name = "";

            if (data.enableCustomCountdowns) {
                boolean mapContainsMaterial = data.countdowns.containsKey(material);

                if (mapContainsMaterial) {
                    Map<String, DataContainer> filterMap = data.countdowns.get(material);

                    boolean mapContainsName = filterMap.containsKey(name);
                    if (mapContainsName) {
                        timer = filterMap.get(name).getTimer();
                        format = filterMap.get(name).getFormat();
                    }

                    boolean mapContainsVoid = filterMap.containsKey("");
                    if (mapContainsVoid & name.equals("") & !mapContainsName) {
                        DataContainer dataContainer = filterMap.get("");

                        timer = dataContainer.getTimer();
                        format = dataContainer.getFormat();
                    }

                    boolean mapContainsAny = filterMap.containsKey("*");
                    if (mapContainsAny & (!"".equals(name) || !mapContainsVoid) & !mapContainsName) {
                        DataContainer dataContainer = filterMap.get("*");
                        timer = dataContainer.getTimer();
                        format = dataContainer.getFormat();
                    }

                }
            }
            DataContainer dataContainer = new DataContainer(timer, format);

            if (data.savePlayerDeathDroppedItems)
                if (data.deathDroppedItemsList.contains(item.getItemStack())) {
                    removeTimer(item, dataContainer);
                    return;
                }

            if (timer == -1) {
                removeTimer(item, dataContainer);
                return;
            }

            setTimer(item, dataContainer);
            return;
        }
        DataContainer dataContainer = (DataContainer) metadataCountdowns.get(0).value();
        dataContainer.setTimer(dataContainer.getTimer() - 1);
        setTimer(item, dataContainer);
    }
}
