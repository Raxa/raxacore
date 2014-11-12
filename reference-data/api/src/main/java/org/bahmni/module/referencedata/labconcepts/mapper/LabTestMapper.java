package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.openmrs.Concept;

public class LabTestMapper extends ResourceMapper {
    public LabTestMapper() {
        super(AllTestsAndPanels.ALL_TESTS_AND_PANELS);
    }

    @Override
    public LabTest map(Concept testConcept) {
        LabTest test = new LabTest();
        test = mapResource(test, testConcept);
        test.setDescription(MapperUtils.getDescriptionOrName(testConcept));
        test.setResultType(testConcept.getDatatype().getName());
        test.setTestUnitOfMeasure(MapperUtils.getUnits(testConcept));
        test.setSortOrder(getSortWeight(testConcept));
        return test;
    }


}
