package com.demkom58.divinedrop.config.updaters;

import com.demkom58.divinedrop.config.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.function.Consumer;

public class Updater0 implements Consumer<Config> {
    @Override
    public void accept(Config config) {
        final FileConfiguration old = config.getConfig();
        final FileConfiguration upd = new YamlConfiguration();

        upd.set("config-version", 1);
        upd.set("check-updates", true);
        upd.set("format", old.getString("without-countdown-format", "&f%name% &7(x%size%)"));
        upd.set("pickup-items-on-sneak", old.getBoolean("without-countdown-format", false));
        upd.set("lang", old.getString("lang", "en_CA"));

        final ConfigurationSection updMsg = upd.createSection("messages");
        updMsg.set("display-name", old.getString("dname", "Display Name&7: &f%name%"));
        updMsg.set("no-permission", old.getString("no-perms", "&cYou do not have permission to run this command."));
        updMsg.set("unknown-cmd", old.getString("unknown-cmd", "&cYou entered an unknown command."));
        updMsg.set("reloaded", old.getString("reloaded", "&aThe configuration is reloaded."));

        final ConfigurationSection updCleaner = upd.createSection("drop-cleaner");
        updCleaner.set("enabled", true);
        updCleaner.set("format", old.getString("format", "&c[&4%countdown%&c] &f%name% &7(x%size%)"));
        updCleaner.set("timer", old.getInt("timer", 10));
        updCleaner.set("timer-for-loaded-items", old.getBoolean("timer-for-loaded-items", true));
        updCleaner.set("save-player-dropped-items", old.getBoolean("save-player-dropped-items", false));
        updCleaner.set("enable-custom-countdowns", old.getBoolean("enable-custom-countdowns", false));

        final ConfigurationSection oldCustomCountdowns = old.getConfigurationSection("custom-countdowns");

        if (oldCustomCountdowns != null)
            updCleaner.createSection("custom-countdowns", oldCustomCountdowns.getValues(true));

        config.setConfig(upd);
        config.save();
    }
}
