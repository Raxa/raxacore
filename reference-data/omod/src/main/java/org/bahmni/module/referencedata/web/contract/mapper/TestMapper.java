package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.model.event.TestEvent;
import org.bahmni.module.referencedata.web.contract.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import static org.bahmni.module.referencedata.web.contract.mapper.MapperUtils.*;

public class TestMapper extends ResourceMapper {
    public TestMapper() {
        super(TestEvent.TEST_PARENT_CONCEPT_NAME);
    }

    @Override
    public Test map(Concept testConcept) {
        Test test = new Test();
        test = mapResource(test, testConcept);
        test.setDepartment(getDepartment(testConcept));
        test.setDescription(getDescription(testConcept));
        test.setShortName(testConcept.getShortestName(Context.getLocale(), false).getName());
        test.setSample(getSample(testConcept));
        test.setResultType(testConcept.getDatatype().getName());
        return test;
    }


}
