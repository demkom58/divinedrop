package com.demkom58.divinedrop.util;

import com.demkom58.divinedrop.version.SupportedVersion;
import com.demkom58.divinedrop.version.VersionManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#[a-fA-f0-9]{6}");

    private static final Map<ChatColor, Vector> COLOR_MAP = Collections.unmodifiableMap(new HashMap<ChatColor, Vector>() {{
        put(ChatColor.BLACK, new Vector(0, 0, 0));
        put(ChatColor.DARK_BLUE, new Vector(0, 0, 170));
        put(ChatColor.DARK_GREEN, new Vector(0, 170, 0));
        put(ChatColor.DARK_AQUA, new Vector(0, 170, 170));
        put(ChatColor.DARK_RED, new Vector(170, 0, 0));
        put(ChatColor.DARK_PURPLE, new Vector(170, 0, 170));
        put(ChatColor.GOLD, new Vector(255, 170, 0));
        put(ChatColor.GRAY, new Vector(170, 170, 170));
        put(ChatColor.DARK_GRAY, new Vector(85, 85, 85));
        put(ChatColor.BLUE, new Vector(85, 85, 255));
        put(ChatColor.GREEN, new Vector(85, 255, 85));
        put(ChatColor.AQUA, new Vector(85, 255, 255));
        put(ChatColor.RED, new Vector(255, 85, 85));
        put(ChatColor.LIGHT_PURPLE, new Vector(255, 85, 255));
        put(ChatColor.YELLOW, new Vector(255, 255, 85));
        put(ChatColor.WHITE, new Vector(255, 255, 255));
    }});

    public static String colorize(String text) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);

        // If RGB colors available use them, else choose the closest constant color
        if (VersionManager.detectedVersion.isNewer(SupportedVersion.V15R1)) {
            while (matcher.find()) {
                String color = text.substring(matcher.start(), matcher.end());
                text = text.replace(color, ChatColor.of(color.substring(1)) + "");
                matcher = HEX_COLOR_PATTERN.matcher(text);
            }
        } else {
            while (matcher.find()) {
                String color = text.substring(matcher.start(), matcher.end());
                text = text.replace(color, fromHex(Integer.parseInt(color.substring(2), 16)) + "");
                matcher = HEX_COLOR_PATTERN.matcher(text);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String escapeColor(String text) {
        return text.replace('§', '&');
    }

    public static ChatColor fromHex(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return fromRGB(r, g, b);
    }

    public static ChatColor fromRGB(int r, int g, int b) {
        TreeMap<Integer, ChatColor> closest = new TreeMap<>();
        COLOR_MAP.forEach((color, set) -> {
            int red = Math.abs(r - set.getBlockX());
            int green = Math.abs(g - set.getBlockY());
            int blue = Math.abs(b - set.getBlockZ());
            closest.put(red + green + blue, color);
        });
        return closest.firstEntry().getValue();
    }
}
