package com.example.borealis_mobile.model;

import java.util.List;
import java.util.Map;

public class Item extends BaseElement {
    private String category;
    private String slot;
    private String cost;
    private String weight;
    private String armorClass;
    private String strengthReq;

    public Item() {}
    public Item(String name, String type, String id, String source, String description,
                Map<String, String> setters, List<Rule> rules, String category, String slot,
                String cost, String weight, String armorClass, String strengthReq) {
        super(name, type, id, source, description, setters, rules);
        this.category = category;
        this.slot = slot;
        this.cost = cost;
        this.weight = weight;
        this.armorClass = armorClass;
        this.strengthReq = strengthReq;
    }

    public String getCategory() { return category; }
    public String getSlot() { return slot; }
    public String getCost() { return cost; }
    public String getWeight() { return weight; }
    public String getArmorClass() { return armorClass; }
    public String getStrengthReq() { return strengthReq; }

    public void setCategory(String category) { this.category = category; }
    public void setSlot(String slot) { this.slot = slot; }
    public void setCost(String cost) { this.cost = cost; }
    public void setWeight(String weight) { this.weight = weight; }
    public void setArmorClass(String armorClass) { this.armorClass = armorClass; }
    public void setStrengthReq(String strengthReq) { this.strengthReq = strengthReq; }
}
