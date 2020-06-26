package com.demkom58.divinedrop.config;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.updaters.ConfigUpdater;
import com.demkom58.divinedrop.version.VersionManager;
import org.jetbrains.annotations.NotNull;

public class Config extends CustomConfig {
    private final ConfigData configData;
    private final int latestVersion;

    public Config(@NotNull final String name,
                  @NotNull final DivineDrop plugin,
                  @NotNull final VersionManager versionManager,
                  final int latestVersion) {
        super(plugin, name);
        this.configData = new ConfigData(plugin, versionManager);
        this.latestVersion = latestVersion;
    }

    /**
     * Loads configuration and converts it if need.
     * @return true if successfully loaded.
     */
    public boolean load() {
        super.saveDefault();
        super.reloadConfig();
        final int version = getConfig().getInt("config-version", 0);

        if (version != latestVersion) {
            final ConfigUpdater configUpdater = ConfigUpdater.getUpdaterForVersion(version);

            if (configUpdater == null)
                throw new IllegalStateException("Doesn't exist config updater for version " + version);

            configUpdater.getProcessor().accept(this);
            this.load();
            return false;
        }

        configData.updateData(getConfig());
        return true;
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public int getLatestVersion() {
        return latestVersion;
    }
}
