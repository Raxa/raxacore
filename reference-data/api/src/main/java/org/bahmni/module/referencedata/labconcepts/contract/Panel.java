package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.List;

public class Panel extends Resource {
    private String description;
    private List<LabTest> tests;
    private String sampleUuid;
    private Double sortOrder;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LabTest> getTests() {
        return tests;
    }

    public void setTests(List<LabTest> tests) {
        this.tests = tests;
    }

    public String getSampleUuid() {
        return sampleUuid;
    }

    public void setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Double getSortOrder() {
        return sortOrder;
    }
}
