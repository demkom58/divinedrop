package com.demkom58.divinedrop.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-f0-9]{6}");

    public static String colorize(String text) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);

        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, ChatColor.of(color) + "");
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String decolorize(String text) {
        return text.replace('§', '&');
    }
}
