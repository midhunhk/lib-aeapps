package com.ae.apps.lib.sample.models;

public class FeatureInfo {
    private int id;
    private String name;

    private FeatureInfo(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static FeatureInfo of(int id, String name){
        return new FeatureInfo(id, name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
