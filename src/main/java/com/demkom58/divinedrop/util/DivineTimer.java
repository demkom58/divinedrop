package com.demkom58.divinedrop.util;

import com.demkom58.divinedrop.drop.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class DivineTimer {
    private final ItemRegistry registry;
    private final JavaPlugin plugin;
    private final Runnable handler;

    private BukkitTask handleTimer;

    public DivineTimer(@NotNull ItemRegistry registry,
                       @NotNull final JavaPlugin plugin,
                       @NotNull final Runnable handler) {
        this.registry = registry;
        this.plugin = plugin;
        this.handler = handler;
    }

    public boolean start() {
        if (handleTimer != null)
            return false;

        handleTimer = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, () -> {
                    registry.getTimedItems().removeIf(registry::valid);
                    handler.run();
            }, 0L, 20L
        );

        return true;
    }

    public boolean stop() {
        if (handleTimer == null)
            return false;

        handleTimer.cancel();
        handleTimer = null;

        return true;
    }


}
