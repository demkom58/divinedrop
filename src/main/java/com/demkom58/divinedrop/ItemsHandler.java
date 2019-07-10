package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionManager;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ItemsHandler {

    public static final Set<Item> PROCESSING_ITEMS = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
    public static final Set<ItemStack> DEATH_DROP_ITEMS = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    private final DivineDrop plugin;
    private final VersionManager versionManager;
    private final ConfigurationData data;

    private boolean countdownRegistered = false;

    public ItemsHandler(@NotNull final DivineDrop plugin,
                        @NotNull final VersionManager versionManager,
                        @NotNull final ConfigurationData data) {
        this.plugin = plugin;
        this.versionManager = versionManager;
        this.data = data;
    }

    public void registerItems(@NotNull final Entity[] entities) {
        Arrays.stream(entities)
                .filter(entity -> entity instanceof Item)
                .forEach(entity -> PROCESSING_ITEMS.add((Item) entity));
    }

    public void registerDeathDrop(@NotNull final PlayerDeathEvent event) {
        DEATH_DROP_ITEMS.addAll(event.getDrops());
    }

    public void removeTimers() {
        PROCESSING_ITEMS.forEach(item -> item.setCustomName(getLiteCustomName(item)));
    }

    public String getLiteCustomName(@NotNull final Item item) {
        return data.getLiteFormat()
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
            format = data.getLiteFormat();

        if (format.equals(data.getFormat()))
            format = data.getLiteFormat();

        item.setCustomName(format
                .replace(ConfigurationData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(ConfigurationData.NAME_PLACEHOLDER, getDisplayName(item))
        );
        PROCESSING_ITEMS.remove(item);
    }

    public void setTimer(@NotNull final Item item,
                         @NotNull final DataContainer dataContainer) {
        if (dataContainer.getTimer() <= 0) {
            item.remove();
            PROCESSING_ITEMS.remove(item);
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
        if (countdownRegistered)
            return;

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, () -> PROCESSING_ITEMS.forEach(this::handleItem), 0L, 20L
        );

        this.countdownRegistered = true;
    }

    private void handleItem(@NotNull final Item item) {
        final List<MetadataValue> metadataCountdowns = item.getMetadata(ConfigurationData.METADATA_COUNTDOWN);
        if (metadataCountdowns.isEmpty()) {

            int timer = data.getTimerValue();
            String format = data.getFormat();
            Material material = item.getItemStack().getType();
            String name = item.getItemStack().getItemMeta().getDisplayName();

            if (name == null)
                name = "";

            if (data.isEnableCustomCountdowns()) {
                final Map<Material, Map<String, DataContainer>> countdowns = data.getCountdowns();
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
                if (DEATH_DROP_ITEMS.contains(item.getItemStack())) {
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
