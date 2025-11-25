package com.example.borealis_mobile.model;

import java.util.List;
import java.util.Map;

public class Class extends BaseElement{
    private String hitDice;
    private String startingGold;
    private List<String> proficiencies;
    private List<String> savingThrows;

    public Class() { }

    public Class(String name, String type, String id, String source, String description,
                 Map<String, String> setters, List<Rule> rules,
                 String hitDice, String startingGold, List<String> proficiencies, List<String> saveProficiencies) {
        super(name, type, id, source, description, setters, rules);
        this.hitDice = hitDice;
        this.startingGold = startingGold;
        this.proficiencies = proficiencies;
        this.savingThrows = saveProficiencies;
    }

    public String getHitDice() { return hitDice; }
    public String getStartingGold() { return startingGold; }
    public List<String> getProficiencies() { return proficiencies; }
    public List<String> getSavingThrows() { return savingThrows; }

    public void setHitDice(String hitDice) { this.hitDice = hitDice; }
    public void setStartingGold(String startingGold) { this.startingGold = startingGold; }
    public void setProficiencies(List<String> proficiencies) { this.proficiencies = proficiencies; }
    public void setSavingThrows(List<String> savingThrows) { this.savingThrows = savingThrows; }
}
