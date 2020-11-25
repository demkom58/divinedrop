package com.demkom58.divinedrop.lang;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.cache.CacheStorage;
import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.version.Version;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class LangManager {

    private final DivineDrop plugin;
    private final ConfigData data;
    private final Downloader downloader;
    private final Language language;
    private final Logger logger;

    public LangManager(@NotNull final DivineDrop plugin,
                       @NotNull final ConfigData data) {
        this.plugin = plugin;
        this.data = data;
        this.downloader = new Downloader(data, this);
        this.language = new Language();
        this.logger = plugin.getLogger();
    }

    public boolean manageLang(String lang, Version version) {
        final File langFile = new File(data.getLangPath());
        final File langFolder = new File(plugin.getDataFolder().getAbsolutePath() + "/languages/");

        if (!langFolder.exists() && !langFolder.mkdir()) {
            logger.severe("Can't create languages folder.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            try {
                if (!this.downloadLang(version, lang, langFile, 0, 5)) {
                    printManualDownload(version, lang, langFile);
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    return false;
                }
            } catch (IOException e) {
                logger.severe("Can't download specified lang");
                e.printStackTrace();
            }
        }

        language.updateLangMap(version, data.getLangPath());
        return true;
    }

    /**
     * Downloads language file for exact version.
     *
     * @param version     language version
     * @param lang        language name
     * @param langFile    file to save
     * @param attempt     download attempt
     * @param maxAttempts max count of attempts to download
     * @return true if successfully
     * @throws IOException on not handled errors
     */
    private boolean downloadLang(@NotNull final Version version,
                                 @NotNull final String lang,
                                 @NotNull final File langFile,
                                 int attempt, int maxAttempts) throws IOException {
        try {
            downloader.downloadResource(version, lang, langFile);
            return true;
        } catch (UnknownHostException e) {
            return false;
        } catch (SocketTimeoutException e) {
            if (attempt >= maxAttempts)
                return false;

            return this.downloadLang(version, lang, langFile, ++attempt, maxAttempts);
        }
    }

    private void printManualDownload(@NotNull final Version version,
                                     @NotNull final String lang,
                                     @NotNull final File langFile) {
        final String fullPath = langFile.getParentFile().getPath();
        final int pluginsIdx = fullPath.indexOf("plugins");

        final String relativePath = pluginsIdx == -1 ? fullPath : fullPath.substring(pluginsIdx);
        final String langFileName = langFile.getName();

        logger.severe("Looks like your server hasn't connection to Internet.");
        logger.severe("Server should have connection to download language...");
        logger.severe("Your can manually download and put it to \"" + relativePath + "\" with name \"" + langFileName + "\"");
        final String link = CacheStorage.load().getLink(version, lang);

        if (link == null) {
            logger.severe("Can't retrieve \"" + lang + "\" download link for \"" + version.id() + "\" from cache, sorry.");
            logger.severe("Try download language files from place where Internet connection is present...");
        } else logger.severe("You can download it from here: " + link);
    }

}
