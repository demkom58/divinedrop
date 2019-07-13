package com.demkom58.divinedrop.versions;

import com.demkom58.divinedrop.DivineDrop;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class VersionManager {
    private final DivineDrop plugin;
    private Version version;

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
        final SupportedVersion supportedVersion = SupportedVersion.getVersion(nmsVersion);

        if (supportedVersion == null)
            throw new UnsupportedOperationException("Current version: " + nmsVersion + ". This version is not supported!");

        this.version = supportedVersion.getFactory().create(plugin, plugin.getData(), plugin.getLogic());
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

    @NotNull
    private String extractNmsVersion() throws UnsupportedOperationException {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnsupportedOperationException("Can't parse version...", e);
        }
    }

}
