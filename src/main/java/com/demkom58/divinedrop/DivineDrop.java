package com.demkom58.divinedrop;

import com.demkom58.divinedrop.config.Config;
import com.demkom58.divinedrop.lang.LangManager;
import com.demkom58.divinedrop.util.Metrics;
import com.demkom58.divinedrop.util.WebSpigot;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

public final class DivineDrop extends JavaPlugin {

    @Getter
    private final Metrics metrics = new Metrics(this);
    @Getter
    private final WebSpigot webSpigot = new WebSpigot(this, getDescription().getVersion(), StaticData.RESOURCE_ID);

    @Getter
    private final VersionManager versionManager = new VersionManager(this);
    @Getter
    private final Config configuration = new Config("config", this, versionManager, 1);
    @Getter
    private final LangManager langManager = new LangManager(this, configuration.getConfigData());
    @Getter
    private final ItemsHandler logic = new ItemsHandler(this, versionManager, configuration.getConfigData());

    @Override
    public void onEnable() {
        try {
            metrics.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            versionManager.setup();
        } catch (UnsupportedOperationException e) {
            Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] " + ChatColor.RED + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Version version = versionManager.getVersion();

        reloadPlugin(version);

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(version.getListener(), this);
        pluginManager.registerEvents(new CommonListener(this), this);

        Optional.ofNullable(getCommand("divinedrop"))
                .ifPresent(cmd -> cmd.setExecutor(
                        new DivineCommandHandler(this, versionManager, configuration.getConfigData())
                ));

        if (configuration.getConfigData().isCheckUpdates())
            webSpigot.ifOutdated((latestVersion) -> {
                final Logger logger = Bukkit.getLogger();
                logger.info("New version '" + latestVersion + "' detected.");
                logger.info("Please update it on: " + webSpigot.getResourceLink());
            }, false);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        logic.removeTimers();
    }

    public void reloadPlugin(@NotNull final Version version) {
        loadConfig(version);
        logic.unregisterCountdown();

        if (configuration.getConfigData().isCleanerEnabled()) {
            logic.registerCountdown();
        } else {
            ItemsHandler.PROCESSING_ITEMS.forEach(item -> item.removeMetadata(StaticData.METADATA_COUNTDOWN, this));
            ItemsHandler.DEATH_DROP_ITEMS.clear();
            ItemsHandler.PROCESSING_ITEMS.clear();
        }

        getServer().getWorlds().forEach(world -> world.getEntities().stream()
                .filter(entity -> entity instanceof Item)
                .forEach(item -> logic.registerItem((Item) item)));
    }

    public void loadConfig(@NotNull final Version version) {
        configuration.load();
        langManager.manageLang(configuration.getConfigData().getLang(), version);
    }

}
