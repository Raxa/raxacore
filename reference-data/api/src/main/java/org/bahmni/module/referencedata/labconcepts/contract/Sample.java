package org.bahmni.module.referencedata.labconcepts.contract;

public class Sample extends Resource {
    private String shortName;
    public static final String SAMPLE_CONCEPT_CLASS = "Sample";
    private Double sortOrder;
    private TestsAndPanels testsAndPanels;

    public Sample() {
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

    public TestsAndPanels getTestsAndPanels() {
        return testsAndPanels;
    }

    public void setTestsAndPanels(TestsAndPanels testsAndPanels) {
        this.testsAndPanels = testsAndPanels;
    }
}