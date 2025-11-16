package com.example.borealis_mobile.model;

public class Rule {
    private String action;
    private String name;
    private String value;
    private String type;
    private String requirements;

    public Rule() { }

    public Rule(String action, String name, String value, String type, String requirements) {
        this.action = action;
        this.name = name;
        this.value = value;
        this.type = type;
        this.requirements = requirements;
    }

    public String getAction() { return action; }
    public String getName() { return name; }
    public String getValue() { return value; }
    public String getType() { return type; }
    public String getRequirements() { return requirements; }
    public void setAction(String action) { this.action = action; }
    public void setName(String name) { this.name = name; }
    public void setValue(String value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
}
