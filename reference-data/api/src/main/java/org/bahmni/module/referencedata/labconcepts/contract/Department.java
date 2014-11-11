package org.bahmni.module.referencedata.labconcepts.contract;

public class Department extends Resource {
    private String description;
    private TestsAndPanels testsAndPanels;

    public TestsAndPanels getTestsAndPanels() {
        return testsAndPanels;
    }

    public void setTestsAndPanels(TestsAndPanels testsAndPanels) {
        this.testsAndPanels = testsAndPanels;
    }

    public static final String DEPARTMENT_PARENT_CONCEPT_NAME = "Lab Departments";
    public static final String DEPARTMENT_CONCEPT_CLASS = "Department";

    public Department() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
