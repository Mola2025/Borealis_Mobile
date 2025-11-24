package com.example.borealis_mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.borealis_mobile.core.util.DataHolder;

import java.util.HashMap;
import java.util.List;

public class Character implements Parcelable {
    private String id = java.util.UUID.randomUUID().toString();
    private String name;
    private String race;
    private String classTypeId;
    private String level;
    private HashMap<String, Integer> stats;
    private List<String> features;
    private List<String> spells;
    private List<String> inventory;

    public Character() { }
    public Character(String name, String race, String classTypeId, String level,
                     HashMap<String, Integer> stats, List<String> features,
                     List<String> spells, List<String> inventory) {
        this.name = name;
        this.race = race;
        this.classTypeId = classTypeId;
        this.level = level;
        this.stats = stats;
        this.features = features;
        this.spells = spells;
        this.inventory = inventory;
    }
    protected Character(Parcel in) {
        id = in.readString();
        name = in.readString();
        race = in.readString();
        classTypeId = in.readString();
        level = in.readString();
        stats = in.readHashMap(HashMap.class.getClassLoader(), String.class, Integer.class);
        features = in.createStringArrayList();
        spells = in.createStringArrayList();
        inventory = in.createStringArrayList();
    }

    public BaseElement getElement(String Id) {
        if (DataHolder.masterCat == null) {
            System.err.println("masterCat is null");
            return null;
        }
        return DataHolder.masterCat.get(Id);
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(race);
        dest.writeString(classTypeId);
        dest.writeString(level);
        dest.writeMap(stats);
        dest.writeStringList(features);
        dest.writeStringList(spells);
        dest.writeStringList(inventory);
    }

    public String getCharName() {
        return name;
    }
    public void setCharName(String name) {
        this.name = name;
    }
    public String getRace() {
        return race;
    }
    public void setRace(String race) {
        this.race = race;
    }
    public String getClassTypeId() {
        return classTypeId;
    }
    public void setClassTypeId(String classTypeId) {
        this.classTypeId = classTypeId;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public HashMap<String, Integer> getStats() {
        return stats;
    }
    public void setStats(HashMap<String, Integer> stats) {
        this.stats = stats;
    }
    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    public List<String> getSpells() {
        return spells;
    }
    public void setSpells(List<String> spells) {
        this.spells = spells;
    }
    public List<String> getInventory() {
        return inventory;
    }
    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
    }

    public String getId() {
        return id;
    }
}
