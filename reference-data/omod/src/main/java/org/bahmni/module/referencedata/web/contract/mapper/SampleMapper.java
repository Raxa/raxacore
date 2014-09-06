package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.model.event.SampleEvent;
import org.bahmni.module.referencedata.web.contract.Sample;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class SampleMapper extends ResourceMapper {
    public SampleMapper() {
        super(SampleEvent.SAMPLE_PARENT_CONCEPT_NAME);
    }

    @Override
    public Sample map(Concept sampleConcept) {
        Sample sample = new Sample();
        sample = mapResource(sample, sampleConcept);
        sample.setShortName(sampleConcept.getShortestName(Context.getLocale(), false).getName());
        return sample;
    }
}