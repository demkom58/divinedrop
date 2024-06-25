package com.demkom58.divinedrop.version;

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
import com.demkom58.divinedrop.version.V19R1.V19R1;
import com.demkom58.divinedrop.version.V19R2.V19R2;
import com.demkom58.divinedrop.version.V19R3.V19R3;
import com.demkom58.divinedrop.version.V20R1.V20R1;
import com.demkom58.divinedrop.version.V20R2.V20R2;
import com.demkom58.divinedrop.version.V20R3.V20R3;
import com.demkom58.divinedrop.version.V8R3.V8R3;
import com.demkom58.divinedrop.version.V8R3.V8ResourceClient;
import com.demkom58.divinedrop.version.V9R1.V9R1;
import com.demkom58.divinedrop.version.V9R2.V9R2;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum SupportedVersion {
    V8R3(V8R3.class, new V8ResourceClient("1.8.9"), V8R3::new),
    V9R1(V9R1.class, new V8ResourceClient("1.9"), V9R1::new),
    V9R2(V9R2.class, new V8ResourceClient("1.9"), V9R2::new),
    V10R1(V10R1.class, new V8ResourceClient("1.10"), V10R1::new),
    V11R1(V11R1.class, new V11ResourceClient("1.11"), V11R1::new),
    V12R1(V12R1.class, new V11ResourceClient("1.12"), V12R1::new),
    V13R1(V13R1.class, new V13ResourceClient("1.13"), V13R1::new),
    V13R2(V13R2.class, new V13ResourceClient("1.13.1"), V13R2::new),
    V14R1(V14R1.class, new V13ResourceClient("1.14"), V14R1::new),
    V15R1(V15R1.class, new V13ResourceClient("1.15"), V15R1::new),
    V16R1(V16R1.class, new V13ResourceClient("1.16"), V16R1::new),
    V16R2(V16R2.class, new V13ResourceClient("1.16.2"), V16R2::new),
    V16R3(V16R3.class, new V13ResourceClient("1.16.4"), V16R3::new),
    V17R1(V17R1.class, new V13ResourceClient("1.17"), V17R1::new),
    V18R1(V18R1.class, new V13ResourceClient("1.18"), V18R1::new),
    V18R2(V18R2.class, new V13ResourceClient("1.18.2"), V18R2::new),
    V19R1(V19R1.class, new V13ResourceClient("1.19"), V19R1::new),
    V19R2(V19R2.class, new V13ResourceClient("1.19.3"), V19R2::new),
    V19R3(V19R3.class, new V13ResourceClient("1.19.4"), V19R3::new),
    V20R1(V20R1.class, new V13ResourceClient("1.20"), V20R1::new),
    V20R2(V20R2.class, new V13ResourceClient("1.20.2"), V20R2::new),
    V20R3(V20R3.class, new V13ResourceClient("1.20.4"), V20R3::new),
    ;

    private static final Map<Class<? extends Version>, SupportedVersion> CLASS_VERSION_MAP = new HashMap<Class<? extends Version>, SupportedVersion>() {{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.versionClass, version);
    }};

    private final Class<? extends Version> versionClass;
    private final Version.ResourceClient client;
    private final VersionFactory factory;

    SupportedVersion(@NotNull final Class<? extends Version> versionClass,
                     @NotNull final Version.ResourceClient client,
                     @NotNull final VersionFactory factory) {
        this.versionClass = versionClass;
        this.client = client;
        this.factory = factory;
    }

    public boolean isNewer(@NotNull final SupportedVersion version) {
        return ordinal() > version.ordinal();
    }

    public boolean isOlder(@NotNull final SupportedVersion version) {
        return ordinal() < version.ordinal();
    }

    @NotNull
    public Version create() {
        try {
            return factory.create(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable SupportedVersion getVersion() {
        for (SupportedVersion value : values()) {
            try {
                value.getFactory().create(value.client);
                return value;
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private interface VersionFactory {
        @NotNull
        Version create(@NotNull final Version.ResourceClient client) throws Exception;
    }
}
