package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.util.LocaleUtility;

public class ConceptData {
    private String name;
    private String rootConcept;

    public ConceptData() {
    }

    public ConceptData(Concept concept) {
        this.name = concept.getName(Context.getLocale()).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootConcept() {
        return rootConcept;
    }

    public void setRootConcept(String rootConcept) {
        this.rootConcept = rootConcept;
    }
}
