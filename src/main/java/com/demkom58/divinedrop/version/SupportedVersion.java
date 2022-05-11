package com.demkom58.divinedrop.version;

import com.demkom58.divinedrop.drop.ItemHandler;
import com.demkom58.divinedrop.version.V10R1.V10R1;
import com.demkom58.divinedrop.version.V11R1.V11R1;
import com.demkom58.divinedrop.version.V11R1.V11ResourceClient;
import com.demkom58.divinedrop.version.V12R1.V12R1;
import com.demkom58.divinedrop.version.V13R1.V13R1;
import com.demkom58.divinedrop.version.V13R1.V13ResourceClient;
import com.demkom58.divinedrop.version.V13R2.V13R2;
import com.demkom58.divinedrop.version.V14R1.V14R1;
import com.demkom58.divinedrop.version.V15R1.V15R1;
import com.demkom58.divinedrop.version.V16R1.V16R1;
import com.demkom58.divinedrop.version.V16R2.V16R2;
import com.demkom58.divinedrop.version.V16R3.V16R3;
import com.demkom58.divinedrop.version.V17R1.V17R1;
import com.demkom58.divinedrop.version.V18R1.V18R1;
import com.demkom58.divinedrop.version.V18R2.V18R2;
import com.demkom58.divinedrop.version.V8R3.V8R3;
import com.demkom58.divinedrop.version.V8R3.V8ResourceClient;
import com.demkom58.divinedrop.version.V9R1.V9R1;
import com.demkom58.divinedrop.version.V9R2.V9R2;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum SupportedVersion {
    V8R3(V8R3.class, new V8ResourceClient("1.8.9"), "v1_8_R3", V8R3::new),
    V9R1(V9R1.class, new V8ResourceClient("1.9"), "v1_9_R1", V9R1::new),
    V9R2(V9R2.class, new V8ResourceClient("1.9"), "v1_9_R2", V9R2::new),
    V10R1(V10R1.class, new V8ResourceClient("1.10"), "v1_10_R1", V10R1::new),
    V11R1(V11R1.class, new V11ResourceClient("1.11"), "v1_11_R1", V11R1::new),
    V12R1(V12R1.class, new V11ResourceClient("1.12"), "v1_12_R1", V12R1::new),
    V13R1(V13R1.class, new V13ResourceClient("1.13"), "v1_13_R1", V13R1::new),
    V13R2(V13R2.class, new V13ResourceClient("1.13.1"), "v1_13_R2", V13R2::new),
    V14R1(V14R1.class, new V13ResourceClient("1.14"), "v1_14_R1", V14R1::new),
    V15R1(V15R1.class, new V13ResourceClient("1.15"), "v1_15_R1", V15R1::new),
    V16R1(V16R1.class, new V13ResourceClient("1.16"), "v1_16_R1", V16R1::new),
    V16R2(V16R2.class, new V13ResourceClient("1.16.2"), "v1_16_R2", V16R2::new),
    V16R3(V16R3.class, new V13ResourceClient("1.16.4"), "v1_16_R3", V16R3::new),
    V17R1(V17R1.class, new V13ResourceClient("1.17"), "v1_17_R1", V17R1::new),
    V18R1(V18R1.class, new V13ResourceClient("1.18"), "v1_18_R1", V18R1::new),
    V18R2(V18R2.class, new V13ResourceClient("1.18.2"), "v1_18_R2", V18R2::new)
    ;

    private static final Map<String, SupportedVersion> NMS_VERSION_MAP = new HashMap<String, SupportedVersion>(){{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.nmsName, version);
    }};

    private static final Map<Class<? extends Version>, SupportedVersion> CLASS_VERSION_MAP = new HashMap<Class<? extends Version>, SupportedVersion>(){{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.versionClass, version);
    }};

    @Getter private final Class<? extends Version> versionClass;
    @Getter private final Version.ResourceClient client;
    @Getter private final String nmsName;
    private final VersionFactory factory;

    SupportedVersion(@NotNull final Class<? extends Version> versionClass,
                     @NotNull final Version.ResourceClient client,
                     @NotNull final String nmsName,
                     @NotNull final VersionFactory factory) {
        this.versionClass = versionClass;
        this.client = client;
        this.nmsName = nmsName;
        this.factory = factory;
    }

    public boolean isNewer(@NotNull final SupportedVersion version) {
        return ordinal() > version.ordinal();
    }

    public boolean isOlder(@NotNull final SupportedVersion version) {
        return ordinal() < version.ordinal();
    }

    @NotNull
    public Version create(@NotNull final ItemHandler manager) {
        return factory.create(client, manager);
    }

    public static @Nullable SupportedVersion getVersion(@NotNull final String nmsName) {
        return NMS_VERSION_MAP.get(nmsName);
    }

    public static @Nullable SupportedVersion getVersion(@NotNull final Class<? extends Version> versionClass) {
        return CLASS_VERSION_MAP.get(versionClass);
    }

    private interface VersionFactory {
        @NotNull Version create(@NotNull final Version.ResourceClient client, @NotNull final ItemHandler manager);
    }
}
