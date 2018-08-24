package com.demkom58.divinedrop.versions;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.versions.V10R1.V10R1;
import com.demkom58.divinedrop.versions.V11R1.V11R1;
import com.demkom58.divinedrop.versions.V12R1.V12R1;
import com.demkom58.divinedrop.versions.V13R1.V13R1;
import com.demkom58.divinedrop.versions.V8R3.V8R3;
import com.demkom58.divinedrop.versions.V9R1.V9R1;
import com.demkom58.divinedrop.versions.V9R2.V9R2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionUtil {
    private static Version version;

    public static void setup() {
        String ver = null;

        try {
            ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            Bukkit.getConsoleSender().sendMessage("[" + DivineDrop.getInstance().getDescription().getName() + "] " + ChatColor.RED + "Version cant be loaded! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(DivineDrop.getInstance());
        }

        if(ver != null) version = detectVersion(ver);
        if(version != null) return;

        Bukkit.getConsoleSender().sendMessage("[" + DivineDrop.getInstance().getDescription().getName() + "] " + ChatColor.RED + " Your current version, " + ver + ", is not supported!");
        Bukkit.getPluginManager().disablePlugin(DivineDrop.getInstance());
    }

    public static Version getVersion() {
        return version;
    }

    @Nullable
    private static Version detectVersion(@NotNull final String ver) {
        if ("v1_8_R3".equals(ver)) return new V8R3();
        if ("v1_9_R1".equals(ver)) return new V9R1();
        if ("v1_9_R2".equals(ver)) return new V9R2();
        if ("v1_10_R1".equals(ver)) return new V10R1();
        if ("v1_11_R1".equals(ver)) return new V11R1();
        if ("v1_12_R1".equals(ver)) return new V12R1();
        if ("v1_13_R1".equals(ver)) return new V13R1();
        return null;
    }

}
