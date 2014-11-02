package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllSamples;
import org.openmrs.Concept;

public class AllSamplesMapper extends ResourceMapper {
    public AllSamplesMapper() {
        super(null);
    }

    @Override
    public AllSamples map(Concept allSamplesConcept) {
        AllSamples allSamples = new AllSamples();
        allSamples = mapResource(allSamples, allSamplesConcept);
        allSamples.setDescription(MapperUtils.getDescription(allSamplesConcept));

        for (Concept setMember : allSamplesConcept.getSetMembers()) {
            if (MapperUtils.isSampleConcept(setMember)) {
                SampleMapper sampleMapper = new SampleMapper();
                allSamples.addSample(sampleMapper.map(setMember));
            }
        }
        return allSamples;
    }
}
