package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class LangManager {

    private final DivineDrop plugin;
    private final ConfigurationData data;
    private final Downloader downloader;
    private final Language language;

    public LangManager(@NotNull final DivineDrop plugin,
                       @NotNull final ConfigurationData data) {
        this.plugin = plugin;
        this.data = data;
        this.downloader = new Downloader(data, this);
        this.language = new Language();
    }

    public void downloadLang(String lang, Version version) {
        final String langPath = data.getLangPath();

        try {
            final File langFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/languages/");

            if (!langFolder.exists() && !langFolder.mkdir()) {
                Bukkit.getConsoleSender().sendMessage("[DivineDrop] §cCan't create languages folder.");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }

            final File langFile = new File(langPath);
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                downloader.downloadResource(version, lang, new File(langPath));
            }

            language.updateLangMap(version, data.getLangPath());
        } catch (IOException ex) {
            plugin.getLogger().severe(ex.getMessage());
        }

    }
}
