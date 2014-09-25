package org.bahmni.module.referencedata.labconcepts.contract;

public class Test extends Resource {
    private String shortName;
    private String description;
    private Department department;
    private Sample sample;
    private String resultType;
    private Double salePrice;
    private String testUnitOfMeasure;

    public static final String TEST_PARENT_CONCEPT_NAME = "All_Tests_and_Panels";

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

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

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getTestUnitOfMeasure() {
        return testUnitOfMeasure;
    }

    public void setTestUnitOfMeasure(String testUnitOfMeasure) {
        this.testUnitOfMeasure = testUnitOfMeasure;
    }

}
