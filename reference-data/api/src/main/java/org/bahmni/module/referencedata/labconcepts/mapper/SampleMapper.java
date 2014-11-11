package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllSamples;
import org.bahmni.module.referencedata.labconcepts.contract.Sample;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

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
        sample.setTestsAndPanels(new TestAndPanelMapper().map(sampleConcept));
        return sample;
    }
}