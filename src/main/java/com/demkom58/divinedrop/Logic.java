package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
}
