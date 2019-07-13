package com.ae.apps.lib.sample.models;

import com.ae.apps.lib.sample.features.Features;

public class FeatureInfo {
    private int id;
    private String name;
    private String description;
    private Class featureClass;
    private Features.SpecialFeature specialFeature;

    private FeatureInfo(int id, String name, String description, Class featureClass, Features.SpecialFeature specialFeature) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.featureClass = featureClass;
        this.specialFeature = specialFeature;
    }

    public static FeatureInfo of(int id, String name, String description, Class featureClass) {
        return new FeatureInfo(id, name, description, featureClass, null);
    }

    public static FeatureInfo of(int id, String name, String description, Features.SpecialFeature specialFeature) {
        return new FeatureInfo(id, name, description, null, specialFeature);
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

    public Class getFeatureClass() { return featureClass;  }

    public Features.SpecialFeature getSpecialFeature() { return specialFeature; }
}
