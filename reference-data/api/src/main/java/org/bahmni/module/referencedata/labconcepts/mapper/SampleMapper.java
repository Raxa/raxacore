package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.*;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getMinimalResources;
import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.isOfConceptClass;

public class SampleMapper extends ResourceMapper {
    public SampleMapper() {
        super(AllSamples.ALL_SAMPLES);
    }

    @Override
    public Sample map(Concept sampleConcept) {
        Sample sample = new Sample();
        sample = mapResource(sample, sampleConcept);
        sample.setShortName(sampleConcept.getShortestName(Context.getLocale(), false).getName());
        sample.setSortOrder(getSortWeight(sampleConcept));
        sample.setTests(getMinimalResources(sampleConcept.getSetMembers(), LabTest.LAB_TEST_CONCEPT_CLASS));
        sample.setPanels(getMinimalResources(sampleConcept.getSetMembers(), Panel.LAB_SET_CONCEPT_CLASS));
        return sample;
    }

}