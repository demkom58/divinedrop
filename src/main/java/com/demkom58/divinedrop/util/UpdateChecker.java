package com.demkom58.divinedrop.util;

import com.demkom58.divinedrop.version.SemVer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (json, typeOfT, context) ->
                    ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString())
            )
            .registerTypeAdapter(SemVer.class, (JsonDeserializer<SemVer>) (json, typeOfT, context) ->
                    new SemVer(json.getAsJsonPrimitive().getAsString())
            )
            .create();

    private final JavaPlugin plugin;
    private final SemVer currentVersion;
    private final SemVer currentMinecraftVersion;
    private final String currentLoader;
    private final String resourceId;
    private final URL versionApiEndpoint;

    public UpdateChecker(@NotNull final JavaPlugin plugin,
                         @NotNull final SemVer currentVersion,
                         @NotNull final SemVer currentMinecraftVersion,
                         @NotNull final String currentLoader,
                         @NotNull final String resourceId) {
        this.plugin = plugin;
        this.currentVersion = currentVersion;
        this.currentMinecraftVersion = currentMinecraftVersion;
        this.currentLoader = currentLoader;
        this.resourceId = resourceId;

        try {
            this.versionApiEndpoint = new URL("https://api.modrinth.com/v2/project/" + resourceId + "/version");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkIfOutdated(@NotNull final Consumer<Version> handler, boolean sync) {
        fetchLatestSupportedVersion(latest -> {
            if (latest == null || currentVersion.isNewer(latest.version_number)) return;

            handler.accept(latest);
        }, sync);
    }

    public void fetchLatestSupportedVersion(@NotNull final Consumer<@Nullable Version> handler, boolean sync) {
        new Thread(() -> {
            try {
                final Version version = fetchLatestSupportedVersion();

                if (sync)
                    Bukkit.getScheduler().runTask(plugin, () -> handler.accept(version));
                else
                    handler.accept(version);
            } catch (IOException ignored) { }
        }).start();
    }

    public @Nullable Version fetchLatestSupportedVersion() throws IOException {
        final URLConnection connection = versionApiEndpoint.openConnection();
        connection.setRequestProperty("User-Agent", "DivineDrop Update Checker");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            final Version[] versions = GSON.fromJson(reader, Version[].class);
            Arrays.sort(versions, (v1, v2) -> v2.version_number.compareTo(v1.version_number));

            for (Version version : versions) {
                if (version.game_versions.contains(currentMinecraftVersion.toString())
                        && version.loaders.contains(currentLoader)
                        && version.version_type.equals("release"))
                    return version;
            }

            return null;
        }
    }

    public String getResourceLink() {
        return "https://modrinth.com/plugin/" + resourceId;
    }

    @Getter
    public static class Version {
        Set<String> game_versions;
        Set<String> loaders;
        String id;
        String name;
        SemVer version_number;
        String changelog;
        String version_type;
        ZonedDateTime date_published;
    }
}
