package com.demkom58.divinedrop;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.config.StaticData;
import com.demkom58.divinedrop.util.ColorUtil;
import com.demkom58.divinedrop.version.SupportedVersion;
import com.demkom58.divinedrop.version.VersionManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DivineCommandHandler implements CommandExecutor {
    private final DivineDrop plugin;
    private final VersionManager versionManager;
    private final ConfigData data;

    public DivineCommandHandler(@NotNull final DivineDrop plugin,
                                @NotNull final VersionManager versionManager,
                                @NotNull final ConfigData data) {
        this.plugin = plugin;
        this.versionManager = versionManager;
        this.data = data;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender,
                             @NotNull final Command command,
                             @NotNull final String label,
                             @NotNull final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(StaticData.INFO);
            return true;
        }

        final String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("reload")) {
            reload(sender);
            return true;
        }

        if (subCommand.equalsIgnoreCase("getName")) {
            getName(sender);
            return true;
        }

        if (subCommand.equalsIgnoreCase("size")) {
            size(sender);
            return true;
        }

        sendMessage(sender, data.getUnknownCmdMessage());
        return true;
    }

    private void reload(@NotNull final CommandSender sender) {
        if (!sender.hasPermission("divinedrop.reload")) {
            sendMessage(sender, data.getNoPermissionMessage());
            return;
        }

        if (plugin.reloadPlugin(versionManager.getVersion())) {
            sendMessage(sender, data.getReloadedMessage());
        } else {
            sendMessage(sender, "An error occurred while reloading configurations...");
        }
    }

    private void getName(@NotNull final CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can be used only from game.");
            return;
        }

        if (!sender.hasPermission("divinedrop.getname"))
            return;

        final Player player = (Player) sender;
        @SuppressWarnings("deprecation") // Using "player.getItemInHand()" due legacy version support
        final ItemStack handStack = versionManager.isOlder(SupportedVersion.V9R1)
                ? player.getItemInHand()
                : player.getInventory().getItemInMainHand();
        final String name;

        if (handStack.getType() != Material.AIR) {
            final ItemMeta itemMeta = handStack.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName())
                name = ColorUtil.escapeColor(itemMeta.getDisplayName());
            else
                name = "§7<Empty>";
        } else name = "AIR";

        sendMessage(sender, data.getItemDisplayNameMessage().replace("%name%", name));
    }

    private void size(@NotNull final CommandSender sender) {
        if (!sender.hasPermission("divinedrop.developer"))
            return;

        Set<Item> processingItems = plugin.getItemHandler().getRegistry().getTimedItems();
        sendMessage(sender, "Items to remove: " + processingItems.size());
    }

    private void sendMessage(@NotNull final CommandSender player, @NotNull final String message) {
        player.sendMessage(data.getPrefixMessage() + message);
    }

}
