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
        if (valid(item)) {
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (valid(item)) {
                return;
            }
            item.setCustomNameVisible(true);

            if (!data.isCleanerEnabled() || !data.isAddItemsOnChunkLoad()) {
                item.setCustomName(itemHandler.getFormattedName(item));
                return;
            }

            timedItems.add(item);
        }, 1L);
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
        if (valid(item)) {
            return false;
        }
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
        if (valid(item)) {
            return false;
        }

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
     * @param item - List of {@link ItemStack itemStack} that should be dropped from player.
     */
    public void deathItemsDrop(@NotNull final List<ItemStack> item) {
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
        if (valid(with) || valid(removed)) {
            return true;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (valid(with) || valid(removed)) {
                return;
            }
            if (!data.isCleanerEnabled())
                handleNewTimedItem(with);
            else
                timedItems.remove(removed);
        }, 1L);

        return true;
    }

    private void handleNewTimedItem(@NotNull final Item item) {
        if (valid(item)) {
            return;
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (valid(item)) {
                return;
            }

            item.setCustomNameVisible(true);

            if (!data.isCleanerEnabled()) {
                item.setCustomName(itemHandler.getFormattedName(item));
                return;
            }

            timedItems.add(item);
        }, 1L);
    }

    public boolean valid(@NotNull Item item) {
        return item.getPickupDelay() == 32767;
    }

}
