package com.example.borealis_mobile.core.parser;

import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Rule;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XmlContentParser {
    public List<BaseElement> parseElements(InputStream inputStream) throws Exception {
        List<BaseElement> elements = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, "UTF-8");

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("element")) {
                BaseElement element = parseSingleElement(parser);
                if (element != null) {
                    elements.add(element);
                }
            }
            eventType = parser.next();
        }
        return elements;
    }

    private BaseElement parseSingleElement(XmlPullParser parser) throws Exception {
        String type = parser.getAttributeValue(null, "type");
        String name = parser.getAttributeValue(null, "name");
        String id = parser.getAttributeValue(null, "id");
        String source = parser.getAttributeValue(null, "source");

        String description = "";
        Map<String, String> setters = new HashMap<>();
        List<Rule> rules = new ArrayList<>();

        int eventType = parser.getEventType();

        while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("element"))) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();

                if (tagName.equals("description")) {
                    description = parser.nextText();
                } else if (tagName.equals("setters")) {
                    parseSetters(parser, setters);
                } else if (tagName.equals("rules")) {
                    parseRules(parser, rules);
                } else {
                    skip(parser);
                }
            }
            eventType = parser.next();
        }

        switch (type) {
            case "Class":
                String hitDice = setters.getOrDefault("hd", "d4");
                String startingGold = setters.getOrDefault("starting gold", "0 gp");

                List<String> proficiencies = extractClassProficiencies(rules);
                List<String> saveProficiencies = extractClassSaveThrows(rules);

                return new com.example.borealis_mobile.model.Class(name, type, id, source, description,
                        setters, rules, hitDice, startingGold, proficiencies, saveProficiencies);
            case "Spell":
                int level = Integer.parseInt(Objects.requireNonNull(setters.getOrDefault("level", "0")));
                String school = setters.getOrDefault("school", "N/A");
                String castingTime = setters.getOrDefault("casting time", "1 action");
                String range = setters.getOrDefault("range", "");
                String components = setters.getOrDefault("components", "");
                String duration = setters.getOrDefault("duration", "");

                return new com.example.borealis_mobile.model.Spell(name, type, id, source, description,
                        setters, rules, level, school, castingTime, range, components, duration);
            case "Race":
                String size = setters.getOrDefault("size", "Medium");
                int speed;
                try {
                    String speedStr = Objects.requireNonNull(setters.getOrDefault("speed", "30"))
                            .replaceAll("[^0-9]", "");
                    speed = Integer.parseInt(speedStr.isEmpty() ? "30" : speedStr);
                } catch (NumberFormatException e) {
                    speed = 30;
                }

                Map<String, Integer> abilityScores = extractRaceAbilityScores(rules);
                List<BaseElement> racialTraits = extractRacialTraits(rules);

                return new com.example.borealis_mobile.model.Race(name, type, id, source, description,
                        setters, rules, size, speed, abilityScores, racialTraits);
            case "Item":
                String category = setters.getOrDefault("category", "N/A");
                String slot = setters.getOrDefault("slot", "");
                String cost = setters.getOrDefault("cost", "");
                String weight = setters.getOrDefault("weight", "0");
                String armorClass = setters.getOrDefault("ac", "");
                String strengthReq = setters.getOrDefault("strength", "");

                return new com.example.borealis_mobile.model.Item(name, type, id, source, description,
                        setters, rules, category, slot, cost, weight, armorClass, strengthReq);
            default:
                return new com.example.borealis_mobile.model.BaseElement(name, type, id, source,
                        description, setters, rules);
        }
    }

    private void parseSetters(XmlPullParser parser, Map<String, String> setters) throws Exception {
        int eventType = parser.getEventType();
        while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("setters"))) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("set")) {
                String setName = parser.getAttributeValue(null, "name");
                String setValue = parser.nextText();

                if (setName != null && !setName.isEmpty()) {
                    setters.put(setName, setValue);
                }
            }
            eventType = parser.next();
        }
    }
    private void parseRules(XmlPullParser parser, List<Rule> rules) throws Exception {
        int eventType = parser.getEventType();

        while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("rules"))) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();

                if (tagName.equals("stat") || tagName.equals("grant") || tagName.equals("select")) {
                    Rule rule = new Rule();
                    rule.setAction(tagName);

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attrName = parser.getAttributeName(i);
                        String attrValue = parser.getAttributeValue(i);

                        switch (attrName) {
                            case "name": rule.setRuleName(attrValue); break;
                            case "value": rule.setValue(attrValue); break;
                            case "type": rule.setType(attrValue); break;
                            case "requirements": rule.setRequirements(attrValue); break;
                            case "bonus": rule.setBonus(attrValue); break;
                            case "alt": rule.setAlt(attrValue); break;
                            case "id": rule.setIdReference(attrValue); break;
                        }
                    }
                    rules.add(rule);
                    skip(parser);
                } else {
                    skip(parser);
                }
            }
            eventType = parser.next();
        }
    }
    private List<String> extractClassProficiencies(List<Rule> rules) {
        List<String> proficiencies = new ArrayList<>();
        for (Rule rule : rules) {
            if ("stat".equals(rule.getAction()) && rule.getRuleName() != null && "true".equals(rule.getValue())) {
                String ruleNameLower = rule.getRuleName().toLowerCase();
                if (ruleNameLower.startsWith("proficiency:") && !ruleNameLower.startsWith("proficiency:save:")) {
                    String profName = ruleNameLower.substring("proficiency:".length());
                    proficiencies.add(profName);
                }
            }
        }
        return proficiencies;
    }
    private List<String> extractClassSaveThrows(List<Rule> rules) {
        List<String> saveThrows = new ArrayList<>();
        for (Rule rule : rules) {
            if ("stat".equals(rule.getAction()) && rule.getRuleName() != null && "true".equals(rule.getValue())) {
                String ruleNameLower = rule.getRuleName().toLowerCase();

                if (ruleNameLower.startsWith("proficiency:save:")) {
                    String saveName = ruleNameLower.substring("proficiency:save:".length());
                    saveThrows.add(saveName);
                }
            }
        }
        return saveThrows;
    }
    private Map<String, Integer> extractRaceAbilityScores(List<Rule> rules) {
        Map<String, Integer> abilityScores = new HashMap<>();
        String[] abilities = {"strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"};

        for (Rule rule : rules) {
            if ("stat".equals(rule.getAction()) && rule.getValue() != null) {
                String ruleNameLower = rule.getRuleName() != null ? rule.getRuleName().toLowerCase() : "";

                for (String ability : abilities) {
                    if (ruleNameLower.equals(ability)) {
                        try {
                            int value = Integer.parseInt(rule.getValue().trim());
                            abilityScores.put(ability, value);
                        } catch (NumberFormatException e) {
                            // Not a number, ignore
                        }
                        break;
                    }
                }
            }
        }
        return abilityScores;
    }
    private List<BaseElement> extractRacialTraits(List<Rule> rules) {
        List<BaseElement> racialTraits = new ArrayList<>();

        for (Rule rule : rules) {
            if ("grant".equals(rule.getAction()) &&
                    "Racial Trait".equals(rule.getType()) &&
                    rule.getIdReference() != null &&
                    rule.getRuleName() != null)
            {
                BaseElement traitReference = new BaseElement();
                traitReference.setId(rule.getIdReference());
                traitReference.setName(rule.getRuleName());
                traitReference.setType(rule.getType());

                racialTraits.add(traitReference);
            }
        }
        return racialTraits;
    }
    private void skip(XmlPullParser parser) throws Exception {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException("Not a start tag");
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
