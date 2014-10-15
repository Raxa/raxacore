package org.bahmni.module.referencedata.labconcepts.contract;

public class LabTest extends Resource {
    private String description;
    private Department department;
    private String sampleUuid;
    private String resultType;
    private String testUnitOfMeasure;
    private Double sortOrder;

    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getSampleUuid() {
        return sampleUuid;
    }

    public void setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getTestUnitOfMeasure() {
        return testUnitOfMeasure;
    }

    public void setTestUnitOfMeasure(String testUnitOfMeasure) {
        this.testUnitOfMeasure = testUnitOfMeasure;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

}
