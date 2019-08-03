package com.demkom58.divinedrop.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public abstract class CustomConfig {
    private final Plugin plugin;
    private final String name;

    private FileConfiguration config = null;
    private File configFile = null;

    public CustomConfig(@NotNull final Plugin plugin,
                        @NotNull final String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void reloadConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), name + ".yml");

        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream resource = plugin.getResource(name + ".yml");
        if (resource == null)
            throw new IllegalStateException("Config with name '" + name + "' in jar doesn't exist!");

        YamlConfiguration defConfig =
                YamlConfiguration.loadConfiguration(new InputStreamReader(resource));

        config.setDefaults(defConfig);
    }

    public FileConfiguration getConfig() {
        if (config == null)
            reloadConfig();

        return config;
    }

    public void setConfig(@NotNull final FileConfiguration config) {
        this.config = config;
    }

    public void save() {
        if (config == null || configFile == null)
            return;

        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDefault() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists())
            plugin.saveResource(name + ".yml", false);
    }

    public String getName() {
        return name;
    }
}
