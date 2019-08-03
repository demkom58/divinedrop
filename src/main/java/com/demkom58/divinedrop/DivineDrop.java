package com.demkom58.divinedrop;

import com.demkom58.divinedrop.config.Config;
import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.lang.LangManager;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DivineDrop extends JavaPlugin {

    private final Metrics metrics = new Metrics(this);

    private final VersionManager versionManager = new VersionManager(this);
    private final ConfigData data = new ConfigData(this, versionManager);
    private final LangManager langManager = new LangManager(this, data);
    private final ItemsHandler logic = new ItemsHandler(this, versionManager, data);
    private final Config config = new Config("config", this, versionManager, 1);

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
        getServer().getPluginManager().registerEvents(version.getListener(), this);

        Optional.ofNullable(getCommand("divinedrop"))
                .ifPresent(cmd -> cmd.setExecutor(new DivineCommandHandler(this, versionManager, data)));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        logic.removeTimers();
    }

    public void reloadPlugin(@NotNull final Version version) {
        loadConfig(version);
        logic.unregisterCountdown();

        if (data.isCleanerEnabled()) {
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
        config.saveDefault();
        config.load();

        data.updateData(config.getConfig());
        langManager.manageLang(data.getLang(), version);
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public ConfigData getData() {
        return data;
    }

    public ItemsHandler getLogic() {
        return logic;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }
}
