package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.versions.Version;
import com.demkom58.divinedrop.versions.VersionManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

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
        final File langFile = new File(data.getLangPath());
        final Logger logger = plugin.getLogger();

        try {
            final File langFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/languages/");

            if (!langFolder.exists() && !langFolder.mkdir()) {
                Bukkit.getConsoleSender().sendMessage("[DivineDrop] §cCan't create languages folder.");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }

            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                downloader.downloadResource(version, lang, langFile);
            }

            language.updateLangMap(version, data.getLangPath());
        } catch (UnknownHostException e) {
            final String fullPath = langFile.getParentFile().getPath();
            final int pluginsIdx = fullPath.indexOf("plugins");

            final String relativePath = pluginsIdx == -1 ? fullPath : fullPath.substring(pluginsIdx);
            final String langFileName = langFile.getName();

            logger.severe("Looks like your server hasn't connection to Internet.");
            logger.severe("Server should have connection to download language...");
            logger.severe("Your can manually download and put it to \"" + relativePath + "\" with name \"" + langFileName + "\"");

            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
