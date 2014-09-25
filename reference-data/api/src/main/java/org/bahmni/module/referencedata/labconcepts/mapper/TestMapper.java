package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class TestMapper extends ResourceMapper {
    public TestMapper() {
        super(Test.TEST_PARENT_CONCEPT_NAME);
    }

    @Override
    public Test map(Concept testConcept) {
        Test test = new Test();
        test = mapResource(test, testConcept);
        test.setDepartment(MapperUtils.getDepartment(testConcept));
        test.setDescription(MapperUtils.getDescription(testConcept));
        test.setShortName(testConcept.getShortestName(Context.getLocale(), false).getName());
        test.setSample(MapperUtils.getSample(testConcept));
        test.setResultType(testConcept.getDatatype().getName());
        test.setTestUnitOfMeasure(MapperUtils.getUnits(testConcept));
        return test;
    }


}
