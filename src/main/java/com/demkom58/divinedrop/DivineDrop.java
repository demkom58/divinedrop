package com.demkom58.divinedrop;

import com.demkom58.divinedrop.lang.LangManager;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class DivineDrop extends JavaPlugin {
    private static DivineDrop instance;

    @Override
    public void onEnable() {
        instance = this;
        Data.langManager = new LangManager();
        VersionUtil.setup();

        saveDefaultConfig();
        loadConfig(VersionUtil.getVersion());

        getServer().getPluginManager().registerEvents(VersionUtil.getVersion().getListener(), this);
        getCommand("divinedrop").setExecutor(new DivineCommands());

        registerCountdown();

        if(Data.addItemsOnChunkLoad)
            for(World world : getServer().getWorlds())
                for(Entity entity : world.getEntities())
                    if(entity instanceof Item) Data.ITEMS_LIST.add((Item)entity);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Logic.removeTimers();
    }

    public void loadConfig(Version version) {
        saveDefaultConfig();
        reloadConfig();

        Data.updateData(getConfig());
        Data.langManager.downloadLang(Data.lang, version);

        saveConfig();
    }

    private void registerCountdown() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Item item : Data.ITEMS_LIST) {
                if (item == null) continue;
                getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
                    final List<MetadataValue> metadataCountdowns = item.getMetadata(Data.METADATA_COUNTDOWN);
                    if (metadataCountdowns.isEmpty()) {

                        int timer = Data.timerValue;
                        String format = Data.format;
                        String name = item.getItemStack().getItemMeta().getDisplayName();

                        if (name == null) name = "";
                        Material material = item.getItemStack().getType();
                        if (Data.enableCustomCountdowns) {
                            boolean mapContainsMaterial = Data.countdowns.containsKey(material);
                            if (mapContainsMaterial) {
                                HashMap<String, DataContainer> filterMap = Data.countdowns.get(material);
                                boolean mapContainsName = filterMap.containsKey(name);
                                if (mapContainsName) {
                                    timer = filterMap.get(name).getTimer();
                                    format = filterMap.get(name).getFormat();
                                }
                                boolean mapContainsVoid = filterMap.containsKey("");
                                if (mapContainsVoid & name.equals("") & !mapContainsName) {
                                    timer = filterMap.get("").getTimer();
                                    format = filterMap.get("").getFormat();
                                }
                                boolean mapContainsAny = filterMap.containsKey("*");
                                if (mapContainsAny & (!name.equals("") || !mapContainsVoid) & !mapContainsName) {
                                    timer = filterMap.get("*").getTimer();
                                    format = filterMap.get("*").getFormat();
                                }
                            }
                        }
                        DataContainer dataContainer = new DataContainer(timer, format);

                        if (Data.savePlayerDeathDroppedItems)
                            if (Data.deathDroppedItemsList.contains(item.getItemStack())) {
                                Logic.setItemWithoutTimer(item, dataContainer);
                                return;
                            }

                        if (timer == -1) {
                            Logic.setItemWithoutTimer(item, dataContainer);
                            return;
                        }

                        Logic.setItemWithTimer(item, dataContainer);
                        return;
                    }
                    DataContainer dataContainer = (DataContainer) metadataCountdowns.get(0).value();
                    dataContainer.setTimer(dataContainer.getTimer() - 1);
                    Logic.setItemWithTimer(item, dataContainer);
                });
            }
        }, 0L, 20L);

    }

    public static DivineDrop getInstance() {
        return instance;
    }
}
