package org.bahmni.module.bahmnicore.model.searchParams;

import java.util.List;

public class AdditionalSearchParam {

    public static class Test{
        private String testName;
        private String value;

        public Test(String testName, String value) {
            this.testName = testName;
            this.value = value;
        }

        public Test() {
        }

        public String getTestName() {
            return '"' + testName + '"';
        }

        public void setTestName(String testName)
        {
            this.testName = testName;
        }

        public String getValue() {
            return '"' + value + '"';
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private String additionalSearchHandler;
    private List<Test> tests;

    public AdditionalSearchParam(String additionalSearchHandler, List<Test> tests) {
        this.additionalSearchHandler = additionalSearchHandler;
        this.tests = tests;
    }

    public AdditionalSearchParam() {
    }

    public String getAdditionalSearchHandler() {
        return additionalSearchHandler;
    }

    public void setAdditionalSearchHandler(String additionalSearchHandler) {
        this.additionalSearchHandler = additionalSearchHandler;
    }

    public List<Test> getTests(){
        return tests;
    }

    public void setTests(List<Test> tests){
        this.tests = tests;
    }
}


