package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.model.event.SampleEvent;
import org.bahmni.module.referencedata.web.contract.Sample;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class SampleMapper {
    public Sample map(Concept sampleConcept) {
        Sample sample = new Sample();
        sample.setId(sampleConcept.getUuid());
        sample.setDateCreated(sampleConcept.getDateCreated());
        sample.setIsActive(!sampleConcept.isRetired());
        sample.setLastUpdated(sampleConcept.getDateChanged());
        sample.setName(sampleConcept.getName(Context.getLocale()).getName());
        sample.setShortName(sampleConcept.getShortestName(Context.getLocale(), false).getName());
        sample.setSortOrder(getSortWeight(sampleConcept));
        return sample;
    }

    private double getSortWeight(Concept sampleConcept) {
        List<ConceptSet> conceptSets = Context.getConceptService().getSetsContainingConcept(sampleConcept);
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConceptSet().getName(Context.getLocale()).getName().equals(SampleEvent.SAMPLE_PARENT_CONCEPT_NAME)){
                return conceptSet.getSortWeight() != null ? conceptSet.getSortWeight() : Double.MAX_VALUE;
            }
        }
        return Double.MAX_VALUE;
    }
}