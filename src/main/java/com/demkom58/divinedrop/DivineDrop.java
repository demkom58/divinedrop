package com.demkom58.divinedrop;

import com.demkom58.divinedrop.lang.LangManager;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DivineDrop extends JavaPlugin {
    private final VersionManager versionManager = new VersionManager(this);
    private final Data data = new Data(this, versionManager);
    private final LangManager langManager = new LangManager(this, versionManager, data);
    private final Logic logic = new Logic(this, versionManager, data);

    @Override
    public void onEnable() {
        try {
            versionManager.setup();
        } catch (UnsupportedOperationException e) {
            Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] " + ChatColor.RED + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Version version = versionManager.getVersion();

        saveDefaultConfig();
        loadConfig(version);

        getServer().getPluginManager().registerEvents(version.getListener(), this);
        getCommand("divinedrop").setExecutor(new DivineCommandHandler(this, versionManager, data));

        logic.registerCountdown();

        if (data.addItemsOnChunkLoad) {
            getServer().getWorlds().forEach(world -> world.getEntities().stream()
                    .filter(entity -> entity instanceof Item)
                    .forEach(item -> Data.ITEMS_LIST.add((Item) item))
            );
        }

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        logic.removeTimers();
    }

    public void loadConfig(@NotNull final Version version) {
        saveDefaultConfig();
        reloadConfig();

        data.updateData(getConfig());
        langManager.downloadLang(data.lang, version);

        saveConfig();
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public Data getData() {
        return data;
    }

    public Logic getLogic() {
        return logic;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }
}
