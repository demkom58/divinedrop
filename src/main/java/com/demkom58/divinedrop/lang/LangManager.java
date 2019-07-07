package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.Data;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.versions.Version;

import java.io.File;
import java.io.IOException;

public class LangManager {

    private DivineDrop plugin = DivineDrop.getInstance();
    private Downloader downloader = new Downloader();
    private Language language = new Language();

    public void downloadLang(String lang, Version version) {
        final String langPath = Data.getLangPath();

        try {
            final File langFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/languages/");

            if (!langFolder.exists() && !langFolder.mkdir()) {
                plugin.getServer().getConsoleSender().sendMessage("[DivineDrop] §cCan't create languages folder.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }

            final File langFile = new File(langPath);
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                downloader.downloadResource(lang, new File(langPath));
            }

            language.updateLangMap(version);
        } catch (IOException ex) {
            plugin.getLogger().severe(ex.getMessage());
        }

    }
}
