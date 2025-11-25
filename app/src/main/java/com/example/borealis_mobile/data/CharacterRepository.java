package com.example.borealis_mobile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.example.borealis_mobile.model.Character;

public class CharacterRepository {
    private static final Type CHAR_LIST_TYPE = new TypeToken<List<Character>>() {}.getType();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File charactersFile;

    public CharacterRepository(File contextDir) {
        this.charactersFile = new File(contextDir, "characters.json");
    }

    public void saveCharacters(List<Character> characters) throws IOException {
        try (FileWriter writer = new FileWriter(charactersFile)) {
            gson.toJson(characters, writer);
        }
    }
    public List<Character> loadCharacters() {
        if (!charactersFile.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(charactersFile)) {
            List<Character> characters = gson.fromJson(reader, CHAR_LIST_TYPE);
            return characters == null ? new ArrayList<>() : characters;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
