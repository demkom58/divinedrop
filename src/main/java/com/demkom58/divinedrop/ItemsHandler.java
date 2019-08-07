package com.demkom58.divinedrop;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.versions.VersionManager;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ItemsHandler {

    private final Set<Item> processingItems = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
    private final Set<ItemStack> deathDropItems = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    private final DivineDrop plugin;
    private final VersionManager versionManager;
    private final ConfigData data;

    private boolean countdownRegistered = false;

    private BukkitTask handleTimer;

    public ItemsHandler(@NotNull final DivineDrop plugin,
                        @NotNull final VersionManager versionManager,
                        @NotNull final ConfigData data) {
        this.plugin = plugin;
        this.versionManager = versionManager;
        this.data = data;
    }

    public void registerItems(@NotNull final Collection<Item> items) {
        items.forEach(item -> item.setCustomNameVisible(true));

        if (!data.isCleanerEnabled()) {
            items.forEach(item -> item.setCustomName(getFormattedName(item)));
            return;
        }

        processingItems.addAll(items);
    }

    public void registerItem(@NotNull final Item item) {
        item.setCustomNameVisible(true);

        if (!data.isCleanerEnabled()) {
            item.setCustomName(getFormattedName(item));
            return;
        }

        processingItems.add(item);
    }

    public void registerDeathDrop(@NotNull final PlayerDeathEvent event) {
        deathDropItems.addAll(event.getDrops());
    }

    public void removeTimers() {
        processingItems.forEach(item -> item.setCustomName(getFormattedName(item)));
    }

    public String getFormattedName(@NotNull final Item item) {
        return data.getFormat()
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item));
    }

    public String getDisplayName(@NotNull final Item item) {
        return versionManager.getVersion().getI18NDisplayName(item.getItemStack());
    }

    public void removeTimer(@NotNull final Item item,
                            @NotNull final DataContainer dataContainer) {
        String format = dataContainer.getFormat();

        if (format == null)
            format = data.getFormat();

        if (format.equals(data.getCleanerFormat()))
            format = data.getFormat();

        item.setCustomName(format
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item))
        );
        processingItems.remove(item);
    }

    public void setTimer(@NotNull final Item item,
                         @NotNull final DataContainer dataContainer) {
        if (dataContainer.getTimer() <= 0) {
            item.remove();
            processingItems.remove(item);
        }

        if (dataContainer.getFormat() == null)
            dataContainer.setFormat("");

        item.setMetadata(StaticData.METADATA_COUNTDOWN, new FixedMetadataValue(plugin, dataContainer));
        item.setCustomName(dataContainer.getFormat()
                .replace(StaticData.TIMER_PLACEHOLDER, String.valueOf(dataContainer.getTimer()))
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item))
        );
    }

    public void registerCountdown() {
        if (countdownRegistered)
            return;

        handleTimer = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, () -> processingItems.forEach(this::handleItem), 0L, 20L
        );

        this.countdownRegistered = true;
    }

    public void unregisterCountdown() {
        if (!countdownRegistered)
            return;

        if (handleTimer != null) {
            handleTimer.cancel();
            handleTimer = null;
        }

        this.countdownRegistered = false;
    }

    private void handleItem(@NotNull final Item item) {
        final List<MetadataValue> metadataCountdowns = item.getMetadata(StaticData.METADATA_COUNTDOWN);
        if (metadataCountdowns.isEmpty()) {

            int timer = data.getTimerValue();
            String format = data.getCleanerFormat();
            Material material = item.getItemStack().getType();
            String name = item.getItemStack().getItemMeta().getDisplayName();

            if (name == null)
                name = "";

            if (data.isEnableCustomCountdowns()) {
                final Map<Material, Map<String, DataContainer>> countdowns = data.getCleanerCountdowns();
                boolean mapContainsMaterial = countdowns.containsKey(material);

                if (mapContainsMaterial) {
                    Map<String, DataContainer> filterMap = countdowns.get(material);

                    DataContainer specifiedContainer = filterMap.get(name);
                    if (specifiedContainer != null) {
                        timer = specifiedContainer.getTimer();
                        format = specifiedContainer.getFormat();
                    }

                    DataContainer voidContainer = filterMap.get("");
                    if (voidContainer != null
                            && "".equals(name)
                            && specifiedContainer == null) {
                        timer = voidContainer.getTimer();
                        format = voidContainer.getFormat();
                    }

                    DataContainer dataContainer = filterMap.get("*");
                    if (dataContainer != null
                            && (!"".equals(name) || voidContainer == null)
                            && specifiedContainer == null) {

                        timer = dataContainer.getTimer();
                        format = dataContainer.getFormat();
                    }

                }
            }
            DataContainer dataContainer = new DataContainer(timer, format);

            if (data.isSavePlayerDeathDroppedItems())
                if (deathDropItems.contains(item.getItemStack())) {
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

    public Set<ItemStack> getDeathDropItems() {
        return deathDropItems;
    }

    public Set<Item> getProcessingItems() {
        return processingItems;
    }
}
