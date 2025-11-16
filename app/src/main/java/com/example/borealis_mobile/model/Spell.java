package com.example.borealis_mobile.model;

import java.util.List;
import java.util.Map;

public class Spell extends BaseElement {
    private int level;
    private String school;
    private String castTime;
    private String range;
    private String components;
    private String duration;

    public Spell() {}
    public Spell(String name, String type, String id, String source, String description,
                 Map<String, String> setters, List<Rule> rules, int level, String school,
                 String castTime, String range, String components, String duration) {
        super(name, type, id, source, description, setters, rules);
        this.level = level;
        this.school = school;
        this.castTime = castTime;
        this.range = range;
        this.components = components;
        this.duration = duration;
    }

    public int getLevel() { return level; }
    public String getSchool() { return school; }
    public String getCastTime() { return castTime; }
    public String getRange() { return range; }
    public String getComponents() { return components; }
    public String getDuration() { return duration; }
}
