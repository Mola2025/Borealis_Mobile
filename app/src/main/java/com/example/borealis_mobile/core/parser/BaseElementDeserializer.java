package com.example.borealis_mobile.core.parser;

import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Item;
import com.example.borealis_mobile.model.Race;
import com.example.borealis_mobile.model.Spell;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class BaseElementDeserializer implements JsonDeserializer<BaseElement> {

    @Override
    public BaseElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement typeElement = jsonObject.get("type");

        if (typeElement == null) {
            return context.deserialize(jsonObject, BaseElement.class);
        }

        String type = typeElement.getAsString();

        switch (type) {
            case "Class":
                return context.deserialize(jsonObject, Class.class);
            case "Race":
                return context.deserialize(jsonObject, Race.class);
            case "Spell":
                return context.deserialize(jsonObject, Spell.class);
            case "Armor":
            case "Weapon":
            case "Magic Item":
            case "Item":
                return context.deserialize(jsonObject, Item.class);

            case "Background":
            case "Feat":
            case "Language":
            case "Condition":
            case "Ability Score Improvement":
            case "Racial Trait":
            default:
                return context.deserialize(jsonObject, BaseElement.class);
        }
    }
}
