package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.List;

public class Department extends Resource {
    private String description;
    private List<LabTest> tests;

    public static final String DEPARTMENT_PARENT_CONCEPT_NAME = "Lab Departments";
    public static final String DEPARTMENT_CONCEPT_CLASS = "Department";

    public Department() {
    }

    public List<LabTest> getTests() {
        return tests;
    }

    public void setTests(List<LabTest> tests) {
        this.tests = tests;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
