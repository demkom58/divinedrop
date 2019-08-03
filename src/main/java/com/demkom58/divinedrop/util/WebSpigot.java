package com.demkom58.divinedrop.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

public class WebSpigot {
    private final JavaPlugin plugin;
    private final String currentVersion;
    private final int resourceId;
    private final URL resourceUrl;

    public WebSpigot(@NotNull final JavaPlugin plugin, @NotNull final String currentVersion, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.currentVersion = currentVersion;

        try {
            this.resourceUrl = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void ifOutdated(@NotNull final Consumer<String> handler, boolean sync) {
        getLatestVersion(latest -> {
            if (currentVersion.equals(latest))
                return;

            handler.accept(latest);
        }, sync);
    }

    public void getLatestVersion(@NotNull final Consumer<String> handler, boolean sync) {
        new Thread(() -> {
            try {
                final String version = getLatestVersion();

                if (sync)
                    Bukkit.getScheduler().runTask(plugin, () -> handler.accept(version));
                else
                    handler.accept(version);
            } catch (IOException ignored) { }
        }).start();
    }

    public String getLatestVersion() throws IOException {
        final URLConnection conn = resourceUrl.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.readLine();
        }
    }

    public String getResourceLink() {
        return "https://www.spigotmc.org/resources/" + resourceId + "/";
    }

}
