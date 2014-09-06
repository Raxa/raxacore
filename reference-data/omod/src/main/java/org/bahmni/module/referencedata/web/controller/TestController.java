package org.bahmni.module.referencedata.web.controller;

import org.bahmni.module.referencedata.web.contract.mapper.TestMapper;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/v1/reference-data/test")
public class TestController {
    private ConceptService conceptService;
    private final TestMapper testMapper;

    @Autowired
    public TestController(ConceptService conceptService) {
        testMapper = new TestMapper();
        this.conceptService = conceptService;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public org.bahmni.module.referencedata.web.contract.Test getTest(@PathVariable("uuid") String uuid) {
        final Concept test = conceptService.getConceptByUuid(uuid);
        if (test == null) {
            throw new ConceptNotFoundException("No test concept found with uuid " + uuid);
        }
        return testMapper.map(test);
    }
}
