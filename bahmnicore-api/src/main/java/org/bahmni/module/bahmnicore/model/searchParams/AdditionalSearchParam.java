package org.bahmni.module.bahmnicore.model.searchParams;

public class AdditionalSearchParam {
    private String testName;
    private String value;

    public AdditionalSearchParam(String testName, String value) {
        this.testName = testName;
        this.value = value;
    }

    public AdditionalSearchParam() {
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
