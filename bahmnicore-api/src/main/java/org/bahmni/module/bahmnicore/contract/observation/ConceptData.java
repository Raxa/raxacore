package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Concept;
import org.openmrs.util.LocaleUtility;

public class ConceptData {
    private String name;

    public ConceptData() {
    }

    public ConceptData(Concept concept) {
        this.name = concept.getName(LocaleUtility.getDefaultLocale()).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
