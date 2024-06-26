package com.demkom58.divinedrop.version;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

@Data
public class SemVer implements Comparable<SemVer> {
    private final int major;
    private final @Nullable Integer minor;
    private final @Nullable Integer patch;

    public SemVer(String version) {
        String[] parts = version.split("\\.");
        major = Integer.parseInt(parts[0]);
        minor = parts.length > 1 ? Integer.parseInt(parts[1]) : null;
        patch = parts.length > 2 ? Integer.parseInt(parts[2]) : null;
    }

    public boolean isNewer(SemVer version) {
        if (major > version.major) return true;
        if (major < version.major) return false;
        if (minor != null && version.minor != null) {
            if (minor > version.minor) return true;
            if (minor < version.minor) return false;
        }
        return patch != null && (version.patch == null || patch > version.patch);
    }

    public boolean isOlder(SemVer version) {
        return !isNewer(version) && !equals(version);
    }

    @Override
    public String toString() {
        return major
                + (patch != null ? (minor == null ? "." + 0 : "." + minor) : (minor != null ? "." + minor : ""))
                + (patch != null ? "." + patch : "");
    }

    public static Comparator<SemVer> closestTo(SemVer version) {
        return (o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.isOlder(version) && o2.isNewer(version)) return -1;
            if (o1.isNewer(version) && o2.isOlder(version)) return 1;
            return o1.isNewer(version) ? -1 : 1;
        };
    }

    @Override
    public int compareTo(@NotNull SemVer o) {
        if (major != o.major) return major - o.major;
        if (minor != null && o.minor != null && !minor.equals(o.minor))
            return minor - o.minor;
        return patch != null && o.patch != null ? patch - o.patch : 0;
    }
}
