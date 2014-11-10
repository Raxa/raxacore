package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.ArrayList;
import java.util.List;

public class AllSamples extends Resource {
    private String description;
    private List<Sample> samples = new ArrayList<>();
    public static final String ALL_SAMPLES = "Lab Samples";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void addSample(Sample sample) {
        if (sample != null) {
            this.samples.add(sample);
        }
    }

}
