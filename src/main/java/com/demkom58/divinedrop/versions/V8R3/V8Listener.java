package com.demkom58.divinedrop.versions.V8R3;

import com.demkom58.divinedrop.Data;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.Logic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

public final class V8Listener implements Listener {
    private final DivineDrop plugin;
    private final Data data;
    private final Logic logic;

    public V8Listener(@NotNull final DivineDrop plugin,
                      @NotNull final Data data,
                      @NotNull final Logic logic) {
        this.plugin = plugin;
        this.data = data;
        this.logic = logic;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!data.addItemsOnChunkLoad)
            return;

        logic.registerItems(event.getChunk().getEntities());
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
        if (data.savePlayerDeathDroppedItems)
            data.deathDroppedItemsList.remove(event.getEntity().getItemStack());

        Data.ITEMS_LIST.remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(PlayerPickupItemEvent event) {
        if (data.pickupOnShift) if (!event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            return;
        }

        if (data.savePlayerDeathDroppedItems)
            data.deathDroppedItemsList.remove(event.getItem().getItemStack());

        Data.ITEMS_LIST.remove(event.getItem());
    }

    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        if (data.savePlayerDeathDroppedItems)
            logic.registerDeathDrop(event);
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