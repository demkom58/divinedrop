package com.demkom58.divinedrop.version.V8R3;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class V8LangParser {

    private static Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static Splitter SPLITTER = Splitter.on('=').limit(2);

    public static Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        Map<String, String> langMap = Maps.newHashMap();
        List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);

        for (String next : lines) {
            if (next.isEmpty() || next.charAt(0) == '#')
                continue;

            String[] array = Iterables.toArray(SPLITTER.split(next), String.class);
            if (array != null && array.length == 2) {
                String first = array[0];
                String str = PATTERN.matcher(array[1]).replaceAll("%$1s");
                langMap.put(first, str);
            }
        }

        return langMap;
    }

}
