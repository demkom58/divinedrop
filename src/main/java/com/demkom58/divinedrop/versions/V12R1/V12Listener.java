package com.demkom58.divinedrop.versions.V12R1;

import com.demkom58.divinedrop.Data;
import com.demkom58.divinedrop.Logic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;

public class V12Listener implements Listener {

    public V12Listener() {}

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if(!Data.addItemsOnChunkLoad) return;
        Logic.registerItems(event.getChunk().getEntities());
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
        if(Data.savePlayerDeathDroppedItems) Data.deathDroppedItemsList.remove(event.getEntity().getItemStack());
        Data.ITEMS_LIST.remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(EntityPickupItemEvent event) {
        if(Data.pickupOnShift) if(event.getEntity() instanceof Player) if(!(((Player)event.getEntity()).isSneaking())) {
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