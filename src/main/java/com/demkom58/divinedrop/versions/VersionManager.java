package com.demkom58.divinedrop.versions;

import com.demkom58.divinedrop.ConfigurationData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import com.demkom58.divinedrop.versions.V10R1.V10R1;
import com.demkom58.divinedrop.versions.V11R1.V11R1;
import com.demkom58.divinedrop.versions.V12R1.V12R1;
import com.demkom58.divinedrop.versions.V13R1.V13R1;
import com.demkom58.divinedrop.versions.V13R2.V13R2;
import com.demkom58.divinedrop.versions.V14R1.V14R1;
import com.demkom58.divinedrop.versions.V8R3.V8R3;
import com.demkom58.divinedrop.versions.V9R1.V9R1;
import com.demkom58.divinedrop.versions.V9R2.V9R2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        this.version = detectVersion(nmsVersion);

        if (version == null)
            throw new UnsupportedOperationException("Current version: " + nmsVersion + ". This version is not supported!");
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

    @Nullable
    private Version detectVersion(@NotNull final String ver) {
        final ConfigurationData data = plugin.getData();
        final ItemsHandler logic = plugin.getLogic();

        if ("v1_8_R3".equals(ver)) return new V8R3(plugin, data, logic);
        if ("v1_9_R1".equals(ver)) return new V9R1(plugin, data, logic);
        if ("v1_9_R2".equals(ver)) return new V9R2(plugin, data, logic);
        if ("v1_10_R1".equals(ver)) return new V10R1(plugin, data, logic);
        if ("v1_11_R1".equals(ver)) return new V11R1(plugin, data, logic);
        if ("v1_12_R1".equals(ver)) return new V12R1(plugin, data, logic);
        if ("v1_13_R1".equals(ver)) return new V13R1(plugin, data, logic);
        if ("v1_13_R2".equals(ver)) return new V13R2(plugin, data, logic);
        if ("v1_14_R1".equals(ver)) return new V14R1(plugin, data, logic);

        return null;
    }

}
