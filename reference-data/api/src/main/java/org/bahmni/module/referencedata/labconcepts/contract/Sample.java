package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.List;

public class Sample extends Resource {
    private String shortName;
    public static final String SAMPLE_CONCEPT_CLASS = "Sample";
    private Double sortOrder;
    private List<ResourceReference> tests;
    private List<ResourceReference> panels;

    public List<ResourceReference> getTests() {
        return tests;
    }

    public void setTests(List<ResourceReference> tests) {
        this.tests = tests;
    }

    public List<ResourceReference> getPanels() {
        return panels;
    }

    public void setPanels(List<ResourceReference> panels) {
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