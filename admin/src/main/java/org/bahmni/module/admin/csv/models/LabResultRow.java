package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.StringUtils;

public class LabResultRow {
    private String test;
    private String result;

    public LabResultRow() {
    }

    public LabResultRow(String test, String result) {
        this.test = test;
        this.result = result;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(test) && StringUtils.isBlank(result);
    }
}
