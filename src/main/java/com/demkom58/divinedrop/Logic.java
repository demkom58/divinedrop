package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Logic {
    private Logic() {
    }

    public static void registerItems(@NotNull Entity[] entities) {
        Arrays.stream(entities)
                .filter(Objects::nonNull)
                .filter(entity -> entity instanceof Item)
                .forEach(entity -> Data.ITEMS_LIST.add((Item) entity));
    }

    public static void registerDeathDrop(@NotNull PlayerDeathEvent event) {
        Data.deathDroppedItemsList.addAll(event.getDrops());
    }

    public static void removeTimers() {
        Data.ITEMS_LIST.forEach(item -> item.setCustomName(getLiteCustomName(item)));
    }

    public static String getLiteCustomName(@NotNull Item item) {
        return Data.liteFormat
                .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(Data.NAME_PLACEHOLDER, getDisplayName(item));
    }

    public static String getDisplayName(@NotNull Item item) {
        return VersionUtil.getVersion().getI18NDisplayName(item.getItemStack());
    }

    public static void removeTimer(@NotNull Item item, @NotNull DataContainer dataContainer) {
        String format = dataContainer.getFormat();

        if (format == null)
            format = Data.liteFormat;

        if (format.equals(Data.format))
            format = Data.liteFormat;

        item.setCustomName(format
                .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(Data.NAME_PLACEHOLDER, Logic.getDisplayName(item))
        );
        Data.ITEMS_LIST.remove(item);
    }

    public static void setTimer(@NotNull Item item, @NotNull DataContainer dataContainer) {
        if (dataContainer.getTimer() <= 0) {
            item.remove();
            Data.ITEMS_LIST.remove(item);
        }

        if (dataContainer.getFormat() == null)
            dataContainer.setFormat("");

        item.setMetadata(Data.METADATA_COUNTDOWN, new FixedMetadataValue(DivineDrop.getInstance(), dataContainer));
        item.setCustomName(dataContainer.getFormat()
                .replace(Data.TIMER_PLACEHOLDER, String.valueOf(dataContainer.getTimer()))
                .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(Data.NAME_PLACEHOLDER, Logic.getDisplayName(item))
        );
    }

    static void registerCountdown() {
        Bukkit.getServer().getScheduler().runTaskTimer(DivineDrop.getInstance(), () ->
                Data.ITEMS_LIST.forEach(item -> Bukkit.getServer().getScheduler().runTaskAsynchronously(DivineDrop.getInstance(), () -> {
                    final List<MetadataValue> metadataCountdowns = item.getMetadata(Data.METADATA_COUNTDOWN);
                    if (metadataCountdowns.isEmpty()) {

                        int timer = Data.timerValue;
                        String format = Data.format;
                        Material material = item.getItemStack().getType();
                        String name = item.getItemStack().getItemMeta().getDisplayName();

                        if (name == null)
                            name = "";

                        if (Data.enableCustomCountdowns) {
                            boolean mapContainsMaterial = Data.countdowns.containsKey(material);

                            if (mapContainsMaterial) {
                                Map<String, DataContainer> filterMap = Data.countdowns.get(material);

                                boolean mapContainsName = filterMap.containsKey(name);
                                if (mapContainsName) {
                                    timer = filterMap.get(name).getTimer();
                                    format = filterMap.get(name).getFormat();
                                }

                                boolean mapContainsVoid = filterMap.containsKey("");
                                if (mapContainsVoid & name.equals("") & !mapContainsName) {
                                    timer = filterMap.get("").getTimer();
                                    format = filterMap.get("").getFormat();
                                }

                                boolean mapContainsAny = filterMap.containsKey("*");
                                if (mapContainsAny & (!name.equals("") || !mapContainsVoid) & !mapContainsName) {
                                    timer = filterMap.get("*").getTimer();
                                    format = filterMap.get("*").getFormat();
                                }

                            }
                        }
                        DataContainer dataContainer = new DataContainer(timer, format);

                        if (Data.savePlayerDeathDroppedItems)
                            if (Data.deathDroppedItemsList.contains(item.getItemStack())) {
                                Logic.removeTimer(item, dataContainer);
                                return;
                            }

                        if (timer == -1) {
                            Logic.removeTimer(item, dataContainer);
                            return;
                        }

                        Logic.setTimer(item, dataContainer);
                        return;
                    }
                    DataContainer dataContainer = (DataContainer) metadataCountdowns.get(0).value();
                    dataContainer.setTimer(dataContainer.getTimer() - 1);
                    Logic.setTimer(item, dataContainer);
                })), 0L, 20L);

    }
}
