package com.example.borealis_mobile.model;

import java.util.List;
import java.util.Map;

public class BaseElement {
    private String name;
    private String type;
    private String id;
    private String source;
    private String description;
    private Map<String, String> setters;
    private List<Rule> rules;

    // Constructor for Firebase
    public BaseElement() {}

    public BaseElement(String name, String type, String id, String source, String description, Map<String, String> setters, List<Rule> rules) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.source = source;
        this.description = description;
        this.setters = setters;
        this.rules = rules;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type;}
    public String getId() { return id; }
    public void setId(String id) { this.id = id;}
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, String> getSetters() { return setters; }
    public void setSetters(Map<String, String> setters) { this.setters = setters; }
    public List<Rule> getRules() { return rules; }
    public void setRules(List<Rule> rules) { this.rules = rules; }
}
