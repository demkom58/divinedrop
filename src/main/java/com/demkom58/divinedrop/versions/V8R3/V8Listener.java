package com.demkom58.divinedrop.versions.V8R3;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class V8Listener implements Listener {
    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemsHandler logic;

    public V8Listener(@NotNull final DivineDrop plugin,
                      @NotNull final ConfigData data,
                      @NotNull final ItemsHandler logic) {
        this.plugin = plugin;
        this.data = data;
        this.logic = logic;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!data.isCleanerEnabled() || !data.isAddItemsOnChunkLoad())
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
            logic.getDeathDropItems().remove(event.getEntity().getItemStack());

        logic.getProcessingItems().remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(PlayerPickupItemEvent event) {
        if (data.isPickupOnShift() && !event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            return;
        }

        if (!data.isCleanerEnabled())
            return;

        if (data.isSavePlayerDeathDroppedItems())
            logic.getDeathDropItems().remove(event.getItem().getItemStack());

        logic.getProcessingItems().remove(event.getItem());
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
            logic.getProcessingItems().remove(event.getEntity());
    }
}