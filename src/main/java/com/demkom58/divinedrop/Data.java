package com.demkom58.divinedrop;

import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Data {
    public static final String PREFIX = "§5§lDivineDrop §7> §f";
    public static final String TIMER_PLACEHOLDER = "%countdown%";
    public static final String SIZE_PLACEHOLDER = "%size%";
    public static final String NAME_PLACEHOLDER = "%name%";
    public static final String METADATA_COUNTDOWN = "╚countdown";
    public static final String[] INFO = new String[]{
            "§b",
            "§e§l§m------------------------------------------------- ",
            "§c§lPLUGIN DEVELOPED BY DEMKOM58",
            "§b",
            "§e§lOFFICIAL PAGE:§7§o https://spigotmc.org/resources/51715/",
            "§b",
            "§7§l 1. /dd reload §8§l-§f§l reloads config",
            "§7§l 2. /dd getname §8§l-§f§l getLocName item custom name",
            "§b",
            "§c§lCODED WITH ♥",
            "§e§l§m------------------------------------------------- ",
            "§b"
    };
    public static final Set<Item> ITEMS_LIST = Collections.newSetFromMap(new WeakHashMap<>());

    public String lang;
    public String format;
    public String liteFormat;
    public String noPermMessage;
    public String unknownCmdMessage;
    public String reloadedMessage;
    public String itemDisplayNameMessage;
    public boolean enableCustomCountdowns;
    public boolean addItemsOnChunkLoad;
    public boolean pickupOnShift;
    public boolean savePlayerDeathDroppedItems;
    public int timerValue = 10;

    public Map<Material, Map<String, DataContainer>> countdowns;
    public List<ItemStack> deathDroppedItemsList;

    private final DivineDrop plugin;
    private final VersionManager versionManager;

    public Data(@NotNull final DivineDrop plugin,
                @NotNull final VersionManager versionManager) {
        this.plugin = plugin;
        this.versionManager = versionManager;
    }

    public String getLangPath() {
        return plugin.getDataFolder().getAbsolutePath() + "/languages/" + versionManager.getVersion().name() + "/" + lang + ".lang";
    }

    public void updateData(@NotNull final FileConfiguration conf) {
        countdowns = null;

        lang = conf.getString("lang", "en_CA");
        format = color(conf.getString("format", "&c[&4%countdown%&c] &f%name% &7(x%size%)"));
        liteFormat = color(conf.getString("without-countdown-format", "&f%name% &7(x%size%)"));
        noPermMessage = color(conf.getString("no-perms", "&cYou do not have permission to run this command."));
        unknownCmdMessage = color(conf.getString("unknown-cmd", "&cYou entered an unknown command."));
        reloadedMessage = color(conf.getString("reloaded", "&aThe configuration is reloaded."));
        itemDisplayNameMessage = color(conf.getString("dname", "Display Name&7: &f%name%"));

        enableCustomCountdowns = conf.getBoolean("enable-custom-countdowns", false);
        addItemsOnChunkLoad = conf.getBoolean("timer-for-loaded-items", true);
        pickupOnShift = conf.getBoolean("pickup-items-on-sneak", false);
        savePlayerDeathDroppedItems = conf.getBoolean("save-player-dropped-items", false);

        timerValue = conf.getInt("timer");

        if (savePlayerDeathDroppedItems)
            deathDroppedItemsList = new ArrayList<>();

        if (enableCustomCountdowns) {
            countdowns = new HashMap<>();
            ConfigurationSection sec = conf.getConfigurationSection("custom-countdowns");
            for (String materialName : sec.getKeys(false)) {
                final Material material = Material.getMaterial(materialName.toUpperCase());

                if (material == null) {
                    Bukkit.getConsoleSender().sendMessage("Unknown material: " + materialName);
                    continue;
                }

                String name = sec.getString(materialName + ".name-filter");
                if (name == null) name = "*";
                name = color(name);

                int timer = sec.getInt(materialName + ".timer");
                String format = sec.getString(materialName + ".format");

                if (format == null)
                    format = Data.this.format;

                format = color(format);
                Map<String, DataContainer> itemFilter;

                if (!countdowns.containsKey(material)) {
                    itemFilter = new HashMap<>();
                    countdowns.put(material, itemFilter);
                } else itemFilter = countdowns.get(material);

                itemFilter.put(name, new DataContainer(timer, format));
            }
        }

    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
