package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Map;

public final class Logic {
    private Logic() { }

    public static void registerNewItems(Entity[] entities) {
        for (Entity entity : entities) {
            if(entity == null) continue;
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (Data.ITEMS_LIST.contains(item)) return;
                Data.ITEMS_LIST.add(item);
            }
        }
    }

    public static void registerDeathDrop(PlayerDeathEvent event) {
        Data.deathDroppedItemsList.addAll(event.getDrops());
    }

    public static void removeTimers() {
        for (Item item : Data.ITEMS_LIST) {
            if(item == null) continue;
            item.setCustomName(Data.liteFormat
                    .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                    .replace(Data.NAME_PLACEHOLDER, getDisplayName(item))
            );
        }
    }

    public static String getDisplayName(Item item) {
        return VersionUtil.getVersion().getI18NDisplayName(item.getItemStack());
    }

    public static void setItemWithoutTimer(Item item, DataContainer dataContainer){
        String format = dataContainer.getFormat();
        if(format == null) format = Data.liteFormat;
        if(format.equals(Data.format)) format = Data.liteFormat;

        item.setCustomName(format
                .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(Data.NAME_PLACEHOLDER, Logic.getDisplayName(item))
        );
        Data.ITEMS_LIST.remove(item);
    }

    public static void setItemWithTimer(Item item, DataContainer dataContainer){
        if(dataContainer.getTimer() <= 0) {
            item.remove();
            Data.ITEMS_LIST.remove(item);
        }
        if(dataContainer.getFormat() == null) dataContainer.setFormat("");
        item.setMetadata(Data.METADATA_COUNTDOWN, new FixedMetadataValue(DivineDrop.getInstance(), dataContainer));
        item.setCustomName(dataContainer.getFormat()
                .replace(Data.TIMER_PLACEHOLDER, String.valueOf(dataContainer.getTimer()))
                .replace(Data.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(Data.NAME_PLACEHOLDER, Logic.getDisplayName(item))
        );
    }

    static void registerCountdown() {
        Bukkit.getServer().getScheduler().runTaskTimer(DivineDrop.getInstance(), () -> {
            for (Item item : Data.ITEMS_LIST) {
                if (item == null) continue;
                Bukkit.getServer().getScheduler().runTaskAsynchronously(DivineDrop.getInstance(), () -> {
                    final List<MetadataValue> metadataCountdowns = item.getMetadata(Data.METADATA_COUNTDOWN);
                    if (metadataCountdowns.isEmpty()) {

                        int timer = Data.timerValue;
                        String format = Data.format;
                        Material material = item.getItemStack().getType();
                        String name = item.getItemStack().getItemMeta().getDisplayName();
                        if (name == null) name = "";

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
                                Logic.setItemWithoutTimer(item, dataContainer);
                                return;
                            }

                        if (timer == -1) {
                            Logic.setItemWithoutTimer(item, dataContainer);
                            return;
                        }

                        Logic.setItemWithTimer(item, dataContainer);
                        return;
                    }
                    DataContainer dataContainer = (DataContainer) metadataCountdowns.get(0).value();
                    dataContainer.setTimer(dataContainer.getTimer() - 1);
                    Logic.setItemWithTimer(item, dataContainer);
                });
            }
        }, 0L, 20L);

    }
}
