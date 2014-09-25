package org.bahmni.module.referencedata.labconcepts.model.event;

import org.openmrs.Concept;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.isSampleConcept;

public class SampleEvent extends ConceptOperationEvent {

    public SampleEvent(String url, String category, String title) {
        super(url, category, title);
    }


    @Override
    public boolean isResourceConcept(Concept concept) {
        return isSampleConcept(concept);
    }


}
