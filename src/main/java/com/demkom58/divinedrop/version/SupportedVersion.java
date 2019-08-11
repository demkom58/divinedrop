package com.demkom58.divinedrop.version;

import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.ItemsHandler;
import com.demkom58.divinedrop.version.V8R3.V8R3;
import com.demkom58.divinedrop.version.V9R1.V9R1;
import com.demkom58.divinedrop.version.V9R2.V9R2;
import com.demkom58.divinedrop.version.V10R1.V10R1;
import com.demkom58.divinedrop.version.V11R1.V11R1;
import com.demkom58.divinedrop.version.V12R1.V12R1;
import com.demkom58.divinedrop.version.V13R1.V13R1;
import com.demkom58.divinedrop.version.V13R2.V13R2;
import com.demkom58.divinedrop.version.V14R1.V14R1;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum SupportedVersion {
    V8R3(V8R3.class, "v1_8_R3", V8R3::new),
    V9R1(V9R1.class, "v1_9_R1", V9R1::new),
    V9R2(V9R2.class, "v1_9_R2", V9R2::new),
    V10R1(V10R1.class, "v1_10_R1", V10R1::new),
    V11R1(V11R1.class, "v1_11_R1", V11R1::new),
    V12R1(V12R1.class, "v1_12_R1", V12R1::new),
    V13R1(V13R1.class, "v1_13_R1", V13R1::new),
    V13R2(V13R2.class, "v1_13_R2", V13R2::new),
    V14R1(V14R1.class, "v1_14_R1", V14R1::new);

    private static final Map<String, SupportedVersion> NMS_VERSION_MAP = new HashMap<String, SupportedVersion>(){{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.nmsName, version);
    }};

    private static final Map<Class<? extends Version>, SupportedVersion> CLASS_VERSION_MAP = new HashMap<Class<? extends Version>, SupportedVersion>(){{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.versionClass, version);
    }};

    @Getter private final Class<? extends Version> versionClass;
    @Getter private final String nmsName;
    @Getter private final VersionFactory factory;

    SupportedVersion(@NotNull final Class<? extends Version> versionClass,
                     @NotNull final String nmsName,
                     @NotNull final VersionFactory factory) {
        this.versionClass = versionClass;
        this.nmsName = nmsName;
        this.factory = factory;
    }

    public static @Nullable SupportedVersion getVersion(@NotNull final String nmsName) {
        return NMS_VERSION_MAP.get(nmsName);
    }

    public static @Nullable SupportedVersion getVersion(@NotNull final Class<? extends Version> versionClass) {
        return CLASS_VERSION_MAP.get(versionClass);
    }

    public interface VersionFactory {
        @NotNull Version create(@NotNull final DivineDrop plugin, @NotNull final ConfigData data, @NotNull final ItemsHandler handler);
    }

    public boolean isNewer(@NotNull final SupportedVersion version) {
        return version.ordinal() > ordinal();
    }

    public boolean isOlder(@NotNull final SupportedVersion version) {
        return version.ordinal() < ordinal();
    }

}
