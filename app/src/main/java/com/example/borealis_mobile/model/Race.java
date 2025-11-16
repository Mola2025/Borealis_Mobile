package com.example.borealis_mobile.model;

import java.util.List;
import java.util.Map;

public class Race extends BaseElement {
    private String size;
    private int speed;
    private Map<String, Integer> abilityScores;
    private List<BaseElement> racialTraits;

    public Race() {}
    public Race(String name, String type, String id, String source, String description,
                Map<String, String> setters, List<Rule> rules, String size, int speed,
                Map<String, Integer> abilityScores, List<BaseElement> racialTraits) {
        super(name, type, id, source, description, setters, rules);
        this.size = size;
        this.speed = speed;
        this.abilityScores = abilityScores;
        this.racialTraits = racialTraits;
    }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public Map<String, Integer> getAbilityScores() { return abilityScores; }
    public void setAbilityScores(Map<String, Integer> abilityScores) { this.abilityScores = abilityScores; }
    public List<BaseElement> getRacialTraits() { return racialTraits; }
    public void setRacialTraits(List<BaseElement> racialTraits) { this.racialTraits = racialTraits; }
}
