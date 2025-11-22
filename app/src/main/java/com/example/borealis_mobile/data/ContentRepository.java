package com.example.borealis_mobile.data;

import com.example.borealis_mobile.core.parser.BaseElementDeserializer;
import com.example.borealis_mobile.model.BaseElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ContentRepository {
    private static final Type CATALOGUE_TYPE = new TypeToken<Map<String, BaseElement>>() {}.getType();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseElement.class, new BaseElementDeserializer())
            .create();

    private final File storage;

    public ContentRepository(File filesDir) {
        this.storage = new File(filesDir, "master_catalogue.json");
    }

    public void saveCatalogue(Map<String, BaseElement> catalogue) throws IOException {
        try (FileWriter writer = new FileWriter(storage)) {
            gson.toJson(catalogue, CATALOGUE_TYPE, writer);
        }
    }
    public Map<String, BaseElement> loadCatalogue() throws FileNotFoundException {
        if (!storage.exists()) {
            return new HashMap<>();
        }
        try (FileReader reader = new FileReader(storage)) {
            return gson.fromJson(reader, CATALOGUE_TYPE);
        } catch (IOException | JsonSyntaxException e) {
            return new HashMap<>();
        }
    }
    public Map<String, BaseElement> loadBasicCatalogue(InputStream inputStream) throws Exception {
        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream)) {
            Map<String, BaseElement> catalogue = gson.fromJson(reader, CATALOGUE_TYPE);
            return catalogue != null ? catalogue : new HashMap<>();
        }
    }
    public Map<String, BaseElement> loadFromAssets(Context context, String fileName) {
        try (InputStream inputStream = context.getAssets().open(fileName)) {
            return loadBasicCatalogue(inputStream);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
