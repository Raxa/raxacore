package org.bahmni.module.bahmnicore.model.searchParams;

import java.util.List;

public class AdditionalSearchParam {

    private String additionalSearchHandler;
    private List<String> tests;

    public AdditionalSearchParam(String additionalSearchHandler, List<String> tests) {
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

    public List<String> getTests(){
        return tests;
    }

    public void setTests(List<String> tests){
        this.tests = tests;
    }
}


