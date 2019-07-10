package com.demkom58.divinedrop.versions.V12R1;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

public class V12Listener implements Listener {

    private final DivineDrop plugin;
    private final ConfigurationData data;
    private final ItemsHandler logic;

    public V12Listener(@NotNull final DivineDrop plugin,
                       @NotNull final ConfigurationData data,
                       @NotNull final ItemsHandler logic) {
        this.plugin = plugin;
        this.data = data;
        this.logic = logic;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!data.isAddItemsOnChunkLoad())
            return;

        logic.registerItems(event.getChunk().getEntities());
    }

    @EventHandler
    public void onDropDeSpawn(ItemDespawnEvent event) {
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

        if (data.isSavePlayerDeathDroppedItems())
            ItemsHandler.DEATH_DROP_ITEMS.remove(event.getItem().getItemStack());

        ItemsHandler.PROCESSING_ITEMS.remove(event.getItem());
    }


    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        if (data.isSavePlayerDeathDroppedItems()) {
            logic.registerDeathDrop(event);
        }
    }


    @EventHandler
    public void onSpawnDrop(ItemSpawnEvent event) {
        ItemsHandler.PROCESSING_ITEMS.add(event.getEntity());
        event.getEntity().setCustomNameVisible(true);

    }

    @EventHandler
    public void onMergeDrop(ItemMergeEvent event) {
        ItemsHandler.PROCESSING_ITEMS.remove(event.getEntity());
    }
}