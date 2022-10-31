package com.demkom58.divinedrop.config;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.version.VersionManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.demkom58.divinedrop.util.ColorUtil.colorize;

@Getter
@Setter
public final class ConfigData {
    private final DivineDrop plugin;
    private final VersionManager versionManager;

    // Common settings
    private boolean checkUpdates;
    private String format;
    private boolean pickupOnShift;
    private boolean ignoreNoPickup;
    private String lang;

    // Message settings
    private String prefixMessage;
    private String itemDisplayNameMessage;
    private String noPermissionMessage;
    private String unknownCmdMessage;
    private String reloadedMessage;

    // Settings for Cleaner
    private boolean cleanerEnabled;
    private String cleanerFormat;
    private int timerValue = 10;
    private boolean addItemsOnChunkLoad;
    private boolean savePlayerDeathDroppedItems;
    private boolean enableCustomCountdowns;

    private Map<Material, Map<String, DataContainer>> cleanerCountdowns;

    public ConfigData(@NotNull final DivineDrop plugin,
                      @NotNull final VersionManager versionManager) {
        this.plugin = plugin;
        this.versionManager = versionManager;
    }

    /**
     * Generates path to version depending
     * on current version of core.
     *
     * @return path to lang file
     */
    public String getLangPath() {
        return plugin.getDataFolder().getAbsolutePath() + "/languages/"
                + versionManager.getVersion().getClient().id() + "/" + lang + ".lang";
    }

    /**
     * Reload all data from configuration file
     * and prepare plugin to work with new configuration.
     *
     * @param conf - configuration file object.
     */
    public void updateData(@NotNull final FileConfiguration conf) {
        cleanerCountdowns = null;

        checkUpdates = conf.getBoolean("check-updates", true);
        format = colorize(conf.getString("format", "&f%name% &7(x%size%)"));
        pickupOnShift = conf.getBoolean("pickup-items-on-sneak", false);
        ignoreNoPickup = conf.getBoolean("ignore-no-pickup-items", true);
        lang = conf.getString("lang", "en_CA");

        final ConfigurationSection msg = getConfigurationSection(conf, "messages");
        prefixMessage = colorize(msg.getString("prefix", "&5&lDivineDrop &7> &f"));
        itemDisplayNameMessage = colorize(msg.getString("display-name", "Display Name&7: &f%name%"));
        noPermissionMessage = colorize(msg.getString("no-permission", "&cYou do not have permission to run this command."));
        unknownCmdMessage = colorize(msg.getString("unknown-cmd", "&cYou entered an unknown command."));
        reloadedMessage = colorize(msg.getString("reloaded", "&aThe configuration is reloaded."));

        final ConfigurationSection cleaner = getConfigurationSection(conf, "drop-cleaner");
        cleanerEnabled = cleaner.getBoolean("enabled", false);
        cleanerFormat = colorize(cleaner.getString("format", "&c[&4%countdown%&c] &f%name% &7(x%size%)"));
        timerValue = cleaner.getInt("timer", 10);
        addItemsOnChunkLoad = cleaner.getBoolean("timer-for-loaded-items", true);
        savePlayerDeathDroppedItems = cleaner.getBoolean("save-player-dropped-items", false);
        enableCustomCountdowns = cleaner.getBoolean("enable-custom-countdowns", false);

        if (!cleanerEnabled) {
            cleanerCountdowns = null;
            return;
        }

        if (enableCustomCountdowns) {
            cleanerCountdowns = new HashMap<>();
            final ConfigurationSection custom = getConfigurationSection(cleaner, "custom-countdowns");
            for (String materialName : custom.getKeys(false)) {
                final Material material = Material.matchMaterial(materialName);

                if (material == null) {
                    Bukkit.getConsoleSender().sendMessage("[DivineDrop] Unknown material: " + materialName);
                    continue;
                }

                String name = custom.getString(materialName + ".name-filter");
                if (name == null)
                    name = "*";
                name = colorize(name);

                int timer = custom.getInt(materialName + ".timer");
                String format = custom.getString(materialName + ".format");

                if (format == null)
                    format = ConfigData.this.cleanerFormat;

                format = colorize(format);
                Map<String, DataContainer> itemFilter;

                if (!cleanerCountdowns.containsKey(material)) {
                    itemFilter = new HashMap<>();
                    cleanerCountdowns.put(material, itemFilter);
                } else
                    itemFilter = cleanerCountdowns.get(material);

                itemFilter.put(name, new DataContainer(timer, format));
            }
        }

    }

    @NotNull
    private ConfigurationSection getConfigurationSection(@NotNull final ConfigurationSection config,
                                                         @NotNull final String name) {
        ConfigurationSection sec = config.getConfigurationSection(name);

        if (sec == null)
            sec = new YamlConfiguration();

        return sec;
    }

}
