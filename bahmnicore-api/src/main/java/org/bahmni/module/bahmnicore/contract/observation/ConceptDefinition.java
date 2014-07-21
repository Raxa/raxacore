package org.bahmni.module.bahmnicore.contract.observation;

import org.openmrs.Concept;

import java.util.ArrayList;
import java.util.List;

public class ConceptDefinition {
    private List<ConceptData> concepts = new ArrayList<>();

    public void add(ConceptData conceptData) {
        concepts.add(conceptData);
    }

    public int getSortWeightFor(Concept observationConcept) {
        int sortWeight = 1;
        for (ConceptData aConcept : concepts) {
            if (aConcept.getName().equalsIgnoreCase(observationConcept.getName().getName())) {
                return sortWeight;
            } else {
                sortWeight++;
            }
        }
        return -1;
    }

    public int size() {
        return concepts.size();
    }
}
