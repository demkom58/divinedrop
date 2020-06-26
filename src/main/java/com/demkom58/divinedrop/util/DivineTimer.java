package com.demkom58.divinedrop.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class DivineTimer {
    private final JavaPlugin plugin;
    private final Runnable handler;

    private BukkitTask handleTimer;

    public DivineTimer(@NotNull final JavaPlugin plugin,
                       @NotNull final Runnable handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    public boolean start() {
        if (handleTimer != null)
            return false;

        handleTimer = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, handler, 0L, 20L
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
