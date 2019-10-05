package com.demkom58.divinedrop.drop;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.ConfigData;
import com.google.common.collect.MapMaker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ItemRegistry {

    @Getter private final Set<Item> timedItems = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
    @Getter private final Set<ItemStack> deathDropItems = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    private final DivineDrop plugin;
    private final ConfigData data;
    private final ItemHandler itemHandler;

    public ItemRegistry(@NotNull final DivineDrop plugin,
                        @NotNull final ConfigData data,
                        @NotNull final ItemHandler itemHandler) {
        this.plugin = plugin;
        this.data = data;
        this.itemHandler = itemHandler;
    }

    /**
     * Calls from item spawn event.
     *
     * @param item - item that was spawned.
     *
     * @return true to allow spawn.
     */
    public boolean spawnedItem(@NotNull final Item item) {
        handleNewTimedItem(item);
        return true;
    }

    /**
     * Calls from chunk load event.
     * @param item - item that was loaded.
     */
    public void loadedItem(@NotNull final Item item) {
        item.setCustomNameVisible(true);

        if (!data.isCleanerEnabled() || !data.isAddItemsOnChunkLoad()) {
            item.setCustomName(itemHandler.getFormattedName(item));
            return;
        }

        timedItems.add(item);
    }

    /**
     * Calls from item despawn event.
     *
     * @param item - item entity that should be despawned.
     *
     * @return true if allow despawn.
     */
    @SuppressWarnings("Duplicates")
    public boolean deSpawnedItem(@NotNull final Item item) {
        if (!data.isCleanerEnabled())
            return true;

        if (data.isSavePlayerDeathDroppedItems())
            deathDropItems.remove(item.getItemStack());

        timedItems.remove(item);
        return true;
    }

    /**
     * Calls on item pickup event.
     *
     * @param entity - entity that picks up item.
     * @param item - item that should be picked up.
     *
     * @return true if allowed to pickup
     */
    @SuppressWarnings("Duplicates")
    public boolean itemPickup(@NotNull final Entity entity, @NotNull final Item item) {
        if (!(entity instanceof Player))
            return true;

        final Player player = (Player) entity;

        if (data.isPickupOnShift() && !player.isSneaking())
            return false;

        if (!data.isCleanerEnabled())
            return true;

        if (data.isSavePlayerDeathDroppedItems())
            deathDropItems.remove(item.getItemStack());

        timedItems.remove(item);
        return true;
    }

    /**
     * Class from Player death event.
     *
     * @param player - died player.
     * @param item - List of {@link ItemStack itemStack} that should be dropped from player.
     */
    public void deathItemsDrop(@NotNull final Player player, @NotNull final List<ItemStack> item) {
        if (!data.isCleanerEnabled())
            return;

        if (data.isSavePlayerDeathDroppedItems())
            deathDropItems.addAll(item);
    }

    /**
     * Calls from item merge event.
     *
     * @param with - this stack will be saved.
     * @param removed - that stack will be removed.
     *
     * @return true if allow merge.
     */
    public boolean mergeDrop(@NotNull final Item with, @NotNull final Item removed) {
        if (!data.isCleanerEnabled())
            handleNewTimedItem(with);
        else
            timedItems.remove(removed);

        return true;
    }

    private void handleNewTimedItem(@NotNull final Item item) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            item.setCustomNameVisible(true);

            if (!data.isCleanerEnabled()) {
                item.setCustomName(itemHandler.getFormattedName(item));
                return;
            }

            timedItems.add(item);
        }, 0);
    }

}
