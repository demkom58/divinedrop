package com.demkom58.divinedrop.version;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class VersionManager {
    private final DivineDrop plugin;

    private Version version;
    private SupportedVersion supportedVersion;

    public VersionManager(@NotNull final DivineDrop plugin) {
        this.plugin = plugin;
    }

    /**
     * Prepares all for using version dependent methods.
     *
     * It is recommend to be called in {@link Plugin#onEnable()} method
     * for in order to avoid exceptions and mistakes.
     *
     * @throws UnsupportedOperationException
     *                  throws in situation when version is not supported or when it can't parse version.
     */
    public void setup() throws UnsupportedOperationException {
        final String nmsVersion = extractNmsVersion();
        supportedVersion = SupportedVersion.getVersion(nmsVersion);

        if (supportedVersion == null)
            throw new UnsupportedOperationException("Current version: " + nmsVersion + ". This version is not supported!");

        final Config config = plugin.getConfiguration();
        this.version = supportedVersion.getFactory().create(plugin, config.getConfigData(), plugin.getLogic());
    }

    /**
     * Getter of {@link Version version} version field.
     *
     * Shouldn't be called before {@link VersionManager#setup()}
     * because it sets version.
     *
     * @return detected version that give access to version depended methods.
     */
    @NotNull
    public Version getVersion() {
        return version;
    }

    /**
     * Supported version enum constant.
     *
     * Allows check newer or older version
     * or create new instances.
     *
     * @return version constant
     */
    public SupportedVersion getSupportedVersion() {
        return supportedVersion;
    }

    @NotNull
    private String extractNmsVersion() throws UnsupportedOperationException {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnsupportedOperationException("Can't parse version...", e);
        }
    }

    /**
     * Helps to check whether the current version
     * is newer than the specified version or not.
     *
     * @param version - version against which the check is performed.
     * @return true if newer.
     */
    public boolean isNewer(@NotNull final SupportedVersion version) {
        return supportedVersion.isNewer(version);
    }

    /**
     * Helps to check whether the current version
     * is older than the specified version or not.
     *
     * @param version - version against which the check is performed.
     *
     * @return true if older.
     */
    public boolean isOlder(@NotNull final SupportedVersion version) {
        return supportedVersion.isOlder(version);
    }

}
