package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.ArrayList;
import java.util.List;

public class AllTestsAndPanels extends Resource {
    private String description;
    private List<LabTest> tests= new ArrayList<>();
    private List<Panel> panels= new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<LabTest> getTests() {
        return tests;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    public void addTest(LabTest test) {
        if(test != null){
            this.tests.add(test);
        }
    }

    public void addPanel(Panel panel) {
        if(panel != null){
            this.panels.add(panel);
        }
    }

}
