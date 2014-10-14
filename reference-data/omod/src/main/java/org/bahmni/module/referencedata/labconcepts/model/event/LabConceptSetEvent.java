package org.bahmni.module.referencedata.labconcepts.model.event;

import org.bahmni.module.referencedata.labconcepts.contract.Sample;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class LabConceptSetEvent extends ConceptOperationEvent {

    public LabConceptSetEvent(String conceptUrl, String labCategory, String title) {
        super(conceptUrl, labCategory, title);
    }

    @Override
    public boolean isResourceConcept(Concept concept) {
        return isLaboratoryConcept(concept);
    }


    private boolean isLaboratoryConcept(Concept concept) {
        return concept.getName(Context.getLocale()) != null && concept.getName(Context.getLocale()).getName().equals(Sample.SAMPLE_PARENT_CONCEPT_NAME);
    }
}
