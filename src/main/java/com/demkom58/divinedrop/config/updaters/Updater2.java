package com.demkom58.divinedrop.config.updaters;

import com.demkom58.divinedrop.config.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.function.Consumer;

public class Updater2 implements Consumer<Config> {
    @Override
    public void accept(Config config) {
        final FileConfiguration cfg = config.getConfig();

        cfg.set("config-version", 3);
        cfg.set("ignore-no-pickup-items", true);

        config.save();
    }
}
