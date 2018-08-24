package com.demkom58.divinedrop.versions.V8R3;

import com.demkom58.divinedrop.Data;
import com.demkom58.divinedrop.Logic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public final class V8Listener implements Listener {

    public V8Listener() {}

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if(!Data.addItemsOnChunkLoad) return;
        Logic.registerNewItems(event.getChunk().getEntities());
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
        if(Data.savePlayerDeathDroppedItems) Data.deathDroppedItemsList.remove(event.getEntity().getItemStack());
        Data.ITEMS_LIST.remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(PlayerPickupItemEvent event) {
        if(Data.pickupOnShift) if(!event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            return;
        }
        if(Data.savePlayerDeathDroppedItems) Data.deathDroppedItemsList.remove(event.getItem().getItemStack());
        Data.ITEMS_LIST.remove(event.getItem());
    }

    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        if(Data.savePlayerDeathDroppedItems) {
            Logic.registerDeathDrop(event);
        }
    }

    @EventHandler
    public void onSpawnDrop(ItemSpawnEvent event) {
        Data.ITEMS_LIST.add(event.getEntity());
        event.getEntity().setCustomNameVisible(true);

    }

    @EventHandler
    public void onMergeDrop(ItemMergeEvent event) {
        Data.ITEMS_LIST.remove(event.getEntity());
    }
}