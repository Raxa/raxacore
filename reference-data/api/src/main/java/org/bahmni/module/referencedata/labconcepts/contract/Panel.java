package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.List;

public class Panel extends Resource {
    private String description;
    private List<MinimalResource> tests;
    private Double sortOrder;
    public static final String LAB_SET_CONCEPT_CLASS = "LabSet";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MinimalResource> getTests() {
        return tests;
    }

    public void setTests(List<MinimalResource> tests) {
        this.tests = tests;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Double getSortOrder() {
        return sortOrder;
    }
}
