package com.example.borealis_mobile.model;

public class Rule {
    private String action;
    private String name;
    private String value;
    private String type;
    private String requirements;
    private String bonus;
    private String alt;
    private String idReference;

    public Rule() { }

    public Rule(String action, String name, String value, String type, String requirements, String bonus, String alt, String idReference) {
        this.action = action;
        this.name = name;
        this.value = value;
        this.type = type;
        this.requirements = requirements;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getValue() { return value; }
    public String getType() { return type; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getRuleName() { return name; }
    public void setRuleName(String name) { this.name = name; }
    public void setValue(String value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public String getBonus() { return bonus; }
    public void setBonus(String bonus) { this.bonus = bonus; }
    public String getAlt() { return alt; }
    public void setAlt(String alt) { this.alt = alt; }
    public String getIdReference() { return idReference; }
    public void setIdReference(String idReference) { this.idReference = idReference; }
}
