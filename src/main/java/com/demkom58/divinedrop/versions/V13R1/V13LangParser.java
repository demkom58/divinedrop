package com.demkom58.divinedrop.versions.V13R1;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class V13LangParser {

    @NotNull
    public static Map<String, String> parseLang(@NotNull InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final JsonReader jsonReader = new JsonReader(reader);

        Map<String, String> langNodes = new Gson().fromJson(jsonReader, Map.class);

        jsonReader.close();
        reader.close();

        return langNodes;
    }

}
