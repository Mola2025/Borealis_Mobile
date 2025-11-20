package com.example.borealis_mobile.data;

import com.example.borealis_mobile.core.parser.BaseElementDeserializer;
import com.example.borealis_mobile.core.parser.XmlContentParser;
import com.example.borealis_mobile.core.parser.XmlIndexParser;
import com.example.borealis_mobile.model.BaseElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ContentRepository {
    private static final Type CATALOGUE_TYPE = new TypeToken<Map<String, BaseElement>>() {}.getType();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseElement.class, new BaseElementDeserializer())
            .setPrettyPrinting()
            .create();

    private final File storage;
    private final XmlIndexParser indexParser = new XmlIndexParser();
    private final XmlContentParser contentParser = new XmlContentParser();

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
        } catch (IOException e) {
            return new HashMap<>();
        } catch (JsonSyntaxException e) {
            storage.delete();
            return new HashMap<>();
        }
    }

    public Map<String, BaseElement> loadContentFromIndex(String indexUrl) throws Exception {
        Queue<String> urls = new LinkedList<>();
        urls.add(indexUrl);

        Set<String> visited = new HashSet<>();
        Map<String, BaseElement> catalogue = new HashMap<>();

        while (!urls.isEmpty()) {
            String currentUrl = urls.poll();
            if (visited.contains(currentUrl)) {
                continue;
            }
            visited.add(currentUrl);
            try (InputStream inputStream = fetchInputStream(currentUrl)) {
                assert currentUrl != null;
                if (currentUrl.endsWith(".index")) {
                    List<String> newUrls = indexParser.extractContentFileUrls(inputStream);
                    urls.addAll(newUrls);
                } else if (currentUrl.endsWith(".xml")) {
                    List<BaseElement> elements = contentParser.parseElements(inputStream);
                    for (BaseElement element : elements) {
                        catalogue.put(element.getId(), element);
                    }
                }
            }
        }
        return catalogue;
    }

    private InputStream fetchInputStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        return url.openStream();
    }
}
