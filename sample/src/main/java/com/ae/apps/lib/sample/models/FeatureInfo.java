package com.ae.apps.lib.sample.models;

public class FeatureInfo {
    private int id;
    private String name;
    private String description;

    private FeatureInfo(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static FeatureInfo of(int id, String name, String description) {
        return new FeatureInfo(id, name, description);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
