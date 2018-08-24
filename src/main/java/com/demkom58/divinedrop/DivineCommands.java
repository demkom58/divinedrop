package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DivineCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Data.INFO);
            return true;
        }

        final String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("divinedrop.reload")) {
                sendMessage(sender, Data.noPermMessage);
                return false;
            }

            DivineDrop.getInstance().loadConfig(VersionUtil.getVersion());
            sendMessage(sender, Data.reloadedMessage);
            return true;
        }

        if (subCommand.equalsIgnoreCase("getName")) {
            if (sender.hasPermission("divinedrop.getname")) {
                final Player player = (Player) sender;
                String itemName;

                try {
                    if (DivineDrop.getInstance().getServer().getVersion().contains("1.8"))
                        itemName = player.getItemInHand().getItemMeta().getDisplayName();
                    else itemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                } catch (NullPointerException ex) {
                    sendMessage(sender, Data.PREFIX + Data.itemDisplayNameMessage.replace("$name$", "AIR"));
                    return false;
                }

                if (itemName == null) itemName = "NONAME";

                sendMessage(sender, Data.itemDisplayNameMessage.replace("$name$", itemName.replace('§', '&')));
                return true;
            }
            return true;
        }

        if (subCommand.equalsIgnoreCase("size")) {
            if (sender.hasPermission("divinedrop.developer"))
                sendMessage(sender, "Items to remove: " + Data.ITEMS_LIST.size());
            return true;
        }

        sendMessage(sender, Data.unknownCmdMessage);
        return true;
    }

    private void sendMessage(@NotNull final CommandSender player, @NotNull final String message) {
        player.sendMessage(Data.PREFIX + message);
    }

}
