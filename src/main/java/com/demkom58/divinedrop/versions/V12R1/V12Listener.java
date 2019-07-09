package com.demkom58.divinedrop.versions.V12R1;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.Logic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

public class V12Listener implements Listener {

    private final DivineDrop plugin;
    private final ConfigurationData data;
    private final Logic logic;

    public V12Listener(@NotNull final DivineDrop plugin,
                       @NotNull final ConfigurationData data,
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

        Logic.ITEMS_LIST.remove(event.getEntity());
    }

    @EventHandler
    public void onDropPickup(EntityPickupItemEvent event) {
        if (data.pickupOnShift && event.getEntity() instanceof Player)
            if (!(((Player) event.getEntity()).isSneaking())) {
                event.setCancelled(true);
                return;
            }

        if (data.savePlayerDeathDroppedItems)
            data.deathDroppedItemsList.remove(event.getItem().getItemStack());

        Logic.ITEMS_LIST.remove(event.getItem());
    }


    @EventHandler
    public void onDeathDrop(PlayerDeathEvent event) {
        if (data.savePlayerDeathDroppedItems) {
            logic.registerDeathDrop(event);
        }
    }


    @EventHandler
    public void onSpawnDrop(ItemSpawnEvent event) {
        Logic.ITEMS_LIST.add(event.getEntity());
        event.getEntity().setCustomNameVisible(true);

    }

    @EventHandler
    public void onMergeDrop(ItemMergeEvent event) {
        Logic.ITEMS_LIST.remove(event.getEntity());
    }
}