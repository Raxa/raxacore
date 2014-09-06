package org.bahmni.module.referencedata.web.contract;

import java.util.List;

public class Panel extends Resource {
    private String shortName;
    private String description;
    private List<Test> tests;
    private Sample sample;
    private Double salePrice;

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

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

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
}
