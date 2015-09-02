package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.contract.RadiologyTest;
import org.openmrs.Concept;

public class RadiologyTestMapper extends ResourceMapper {
    public RadiologyTestMapper() {
        super(RadiologyTest.RADIOLOGY_TEST_PARENT_CONCEPT_NAME);
    }

    @Override
    public RadiologyTest map(Concept testConcept) {
        return mapResource(new RadiologyTest(), testConcept);
    }


}
