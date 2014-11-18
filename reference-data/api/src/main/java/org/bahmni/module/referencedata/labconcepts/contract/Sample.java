package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.List;

public class Sample extends Resource {
    private String shortName;
    public static final String SAMPLE_CONCEPT_CLASS = "Sample";
    private Double sortOrder;
    private List<MinimalResource> tests;
    private List<MinimalResource> panels;

    public Sample() {
    }

    public List<MinimalResource> getTests() {
        return tests;
    }

    public void setTests(List<MinimalResource> tests) {
        this.tests = tests;
    }

    public List<MinimalResource> getPanels() {
        return panels;
    }

    public void setPanels(List<MinimalResource> panels) {
        this.panels = panels;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }
}