package com.demkom58.divinedrop;

import com.demkom58.divinedrop.util.WebSpigot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class CommonListener implements Listener {
    private final DivineDrop plugin;

    public CommonListener(@NotNull final DivineDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
        if (!event.getPlayer().isOp())
            return;

        final Player player = event.getPlayer();
        final WebSpigot webSpigot = plugin.getWebSpigot();
        webSpigot.ifOutdated(latest -> {
            player.sendMessage(ChatColor.DARK_PURPLE + "New version of DivineDrop found. Latest: " + latest);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "You can update it here: " + ChatColor.GRAY + webSpigot.getResourceLink());
        }, true);
    }

}
