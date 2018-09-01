package com.demkom58.divinedrop;

import com.demkom58.divinedrop.lang.LangManager;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DivineDrop extends JavaPlugin {
    private static DivineDrop instance;

    @Override
    public void onEnable() {
        DivineDrop.instance = this;
        Data.langManager = new LangManager();
        VersionUtil.setup();

        saveDefaultConfig();
        loadConfig(VersionUtil.getVersion());

        getServer().getPluginManager().registerEvents(VersionUtil.getVersion().getListener(), this);
        getCommand("divinedrop").setExecutor(new DivineCommands());

        Logic.registerCountdown();

        if (Data.addItemsOnChunkLoad) {
            getServer().getWorlds().forEach(world -> world.getEntities().stream()
                    .filter(entity -> entity instanceof Item)
                    .forEach(item -> Data.ITEMS_LIST.add((Item) item))
            );
        }

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Logic.removeTimers();
    }

    public void loadConfig(@NotNull Version version) {
        saveDefaultConfig();
        reloadConfig();

        Data.updateData(getConfig());
        Data.langManager.downloadLang(Data.lang, version);

        saveConfig();
    }


    public static DivineDrop getInstance() {
        return instance;
    }
}
