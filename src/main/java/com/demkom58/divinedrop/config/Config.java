package com.demkom58.divinedrop.config;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.updaters.ConfigUpdater;
import com.demkom58.divinedrop.versions.VersionManager;
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

    public void load() {
        super.reloadConfig();
        final int version = getConfig().getInt("config-version", 0);

        if (version != latestVersion) {
            final ConfigUpdater configUpdater = ConfigUpdater.getUpdaterForVersion(version);

            if (configUpdater == null)
                throw new IllegalStateException("Doesn't exist config updater for version " + version);

            configUpdater.getProcessor().accept(this);
            this.load();
            return;
        }

        configData.updateData(getConfig());
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public int getLatestVersion() {
        return latestVersion;
    }
}
