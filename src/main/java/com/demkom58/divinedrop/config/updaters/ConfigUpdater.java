package com.demkom58.divinedrop.config.updaters;

import com.demkom58.divinedrop.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum ConfigUpdater {
    VERSION_0(0, new Updater0()),
    VERSION_1(1, new Updater1());

    private static final Map<Integer, ConfigUpdater> VERSION_TO_UPDATER = new HashMap<>();

    private final int targetVersion;
    private final Consumer<Config> processor;

    ConfigUpdater(final int targetVersion,
                  @NotNull final Consumer<Config> processor) {
        this.targetVersion = targetVersion;
        this.processor = processor;
    }

    public Consumer<Config> getProcessor() {
        return processor;
    }

    public int getTargetVersion() {
        return targetVersion;
    }

    public static @Nullable ConfigUpdater getUpdaterForVersion(int targetVersion) {
        return VERSION_TO_UPDATER.get(targetVersion);
    }

    static {
        for (ConfigUpdater updaters : values())
            VERSION_TO_UPDATER.put(updaters.targetVersion, updaters);
    }

}
