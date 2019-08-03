package com.demkom58.divinedrop.versions.V12R1;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class V12Listener implements Listener {

    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemsHandler logic;

    public V12Listener(@NotNull final DivineDrop plugin,
                       @NotNull final ConfigData data,
                       @NotNull final ItemsHandler logic) {
        this.plugin = plugin;
        this.data = data;
        this.logic = logic;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!data.isAddItemsOnChunkLoad())
            return;

        List<Item> items = Arrays.stream(event.getChunk().getEntities())
                .filter(entity -> entity instanceof Item)
                .map(item -> (Item) item)
                .collect(Collectors.toList());

        logic.registerItems(items);
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
        if (!data.isCleanerEnabled())
            return;

        if (data.isSavePlayerDeathDroppedItems())
            ItemsHandler.DEATH_DROP_ITEMS.remove(event.getEntity().getItemStack());

        ItemsHandler.PROCESSING_ITEMS.remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(EntityPickupItemEvent event) {
        if (data.isPickupOnShift()
                && event.getEntity() instanceof Player
                && !(((Player) event.getEntity()).isSneaking())) {
            event.setCancelled(true);
            return;
        }

        if (!data.isCleanerEnabled())
            return;

        if (data.isSavePlayerDeathDroppedItems())
            ItemsHandler.DEATH_DROP_ITEMS.remove(event.getItem().getItemStack());

        ItemsHandler.PROCESSING_ITEMS.remove(event.getItem());
    }


    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        if (!data.isCleanerEnabled())
            return;

        if (data.isSavePlayerDeathDroppedItems())
            logic.registerDeathDrop(event);
    }


    @EventHandler
    public void onSpawnDrop(ItemSpawnEvent event) {
        logic.registerItem(event.getEntity());
    }

    @EventHandler
    public void onMergeDrop(ItemMergeEvent event) {
        if (!data.isCleanerEnabled())
            Bukkit.getScheduler().runTaskLater(plugin, () -> logic.registerItem(event.getTarget()), 0);
        else
            ItemsHandler.PROCESSING_ITEMS.remove(event.getEntity());
    }
}