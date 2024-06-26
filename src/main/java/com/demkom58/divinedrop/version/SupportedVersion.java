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
import com.demkom58.divinedrop.version.V21.V21;
import com.demkom58.divinedrop.version.V8R3.V8R3;
import com.demkom58.divinedrop.version.V8R3.V8ResourceClient;
import com.demkom58.divinedrop.version.V9R1.V9R1;
import com.demkom58.divinedrop.version.V9R2.V9R2;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum SupportedVersion {
    V8R3(V8R3.class, "1.8.9", V8ResourceClient::new, V8R3::new),
    V9R1(V9R1.class, "1.9", V8ResourceClient::new, V9R1::new),
    V9R2(V9R2.class, "1.9", V8ResourceClient::new, V9R2::new),
    V10R1(V10R1.class, "1.10", V8ResourceClient::new, V10R1::new),
    V11R1(V11R1.class, "1.11", V11ResourceClient::new, V11R1::new),
    V12R1(V12R1.class, "1.12", V11ResourceClient::new, V12R1::new),
    V13R1(V13R1.class, "1.13", V13ResourceClient::new, V13R1::new),
    V13R2(V13R2.class, "1.13.1", V13ResourceClient::new, V13R2::new),
    V14R1(V14R1.class, "1.14", V13ResourceClient::new, V14R1::new),
    V15R1(V15R1.class, "1.15", V13ResourceClient::new, V15R1::new),
    V16R1(V16R1.class, "1.16", V13ResourceClient::new, V16R1::new),
    V16R2(V16R2.class, "1.16.2", V13ResourceClient::new, V16R2::new),
    V16R3(V16R3.class, "1.16.4", V13ResourceClient::new, V16R3::new),
    V17R1(V17R1.class, "1.17", V13ResourceClient::new, V17R1::new),
    V18R1(V18R1.class, "1.18", V13ResourceClient::new, V18R1::new),
    V18R2(V18R2.class, "1.18.2", V13ResourceClient::new, V18R2::new),
    V19R1(V19R1.class, "1.19", V13ResourceClient::new, V19R1::new),
    V19R2(V19R2.class, "1.19.3", V13ResourceClient::new, V19R2::new),
    V19R3(V19R3.class, "1.19.4", V13ResourceClient::new, V19R3::new),
    V20R1(V20R1.class, "1.20", V13ResourceClient::new, V20R1::new),
    V20R2(V20R2.class, "1.20.2", V13ResourceClient::new, V20R2::new),
    V20R3(V20R3.class, "1.20.4", V13ResourceClient::new, V20R3::new),
    V21(V21.class, "1.21", V13ResourceClient::new, V21::new),
    ;

    private static final Map<Class<? extends Version>, SupportedVersion> CLASS_VERSION_MAP = new HashMap<Class<? extends Version>, SupportedVersion>() {{
        for (SupportedVersion version : SupportedVersion.values())
            put(version.versionClass, version);
    }};

    private final Class<? extends Version> versionClass;
    private final SemVer version;
    private final ClientFactory clientFactory;
    private final VersionFactory versionFactory;

    SupportedVersion(@NotNull final Class<? extends Version> versionClass,
                     @NotNull final String version,
                     @NotNull final ClientFactory clientFactory,
                     @NotNull final VersionFactory versionFactory) {
        this.versionClass = versionClass;
        this.version = new SemVer(version);
        this.clientFactory = clientFactory;
        this.versionFactory = versionFactory;
    }

    public boolean isNewer(@NotNull final SupportedVersion version) {
        return ordinal() > version.ordinal();
    }

    public boolean isOlder(@NotNull final SupportedVersion version) {
        return ordinal() < version.ordinal();
    }

    /**
     * Creates version instance.
     *
     * @param specificVersion specific version to create or null to use default version.
     * @return created version instance.
     */
    public @NotNull Version create(@Nullable SemVer specificVersion) {
        try {
            String version = specificVersion == null ? this.version.toString() : specificVersion.toString();
            Version.ResourceClient client = clientFactory.create(version);
            return versionFactory.create(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable SupportedVersion getVersion(@Nullable SemVer specificVersion) {
        List<SupportedVersion> list = Stream.of(values()).filter(version -> {
            try {
                version.create(specificVersion);
                return true;
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());

        if (specificVersion != null) {
            Comparator<SemVer> semVerComparator = SemVer.closestTo(specificVersion);
            list.sort((o1, o2) -> semVerComparator.compare(o1.version, o2.version));
        }

        return list.isEmpty() ? null : list.get(0);
    }

    private interface VersionFactory {
        @NotNull
        Version create(@NotNull final Version.ResourceClient client) throws Exception;
    }

    public interface ClientFactory {
        @NotNull
        Version.ResourceClient create(@NotNull String version) throws Exception;
    }
}
