package org.bahmni.module.bahmnicore.contract.observation;

import java.util.ArrayList;
import java.util.List;

public class ConceptDefinition {
    private List<ConceptData> concepts = new ArrayList<>();

    public void add(ConceptData conceptData) {
        concepts.add(conceptData);
    }

    public void addAll(List<ConceptData> conceptDatas) {
        concepts.addAll(conceptDatas);
    }

    public int size() {
        return concepts.size();
    }

    public List<ConceptData> getConcepts() {
        return concepts;
    }

}
