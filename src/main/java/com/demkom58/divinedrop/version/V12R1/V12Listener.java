package com.demkom58.divinedrop.version.V12R1;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.drop.ItemRegistry;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class V12Listener implements Listener {

    private final ItemHandler itemHandler;

    public V12Listener(@NotNull final ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        final ItemRegistry registry = itemHandler.getRegistry();

        Arrays.stream(event.getChunk().getEntities())
                .filter(entity -> entity instanceof Item)
                .forEach(entity -> registry.loadedItem((Item) entity));
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
        if (!itemHandler.getRegistry().deSpawnedItem(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDropPickup(EntityPickupItemEvent event) {
        if (!itemHandler.getRegistry().itemPickup(event.getEntity(), event.getItem()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        itemHandler.getRegistry().deathItemsDrop(event.getEntity(), event.getDrops());
    }

    @EventHandler
    public void onSpawnDrop(ItemSpawnEvent event) {
        if (!itemHandler.getRegistry().spawnedItem(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMergeDrop(ItemMergeEvent event) {
        if (!itemHandler.getRegistry().mergeDrop(event.getTarget(), event.getEntity()))
            event.setCancelled(true);
    }
}