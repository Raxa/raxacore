//TODO : MIHIR : Add more ITs for departments, tests and panels only happy path will be fine, and resolve all the TODOS in the Bahmni-Core
package org.bahmni.module.referencedata.advice;

import org.bahmni.module.bahmnicore.web.v1_0.controller.BaseWebControllerTest;
import org.bahmni.module.referencedata.web.contract.Sample;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ConceptOperationEventInterceptorIT extends BaseWebControllerTest {
    @Autowired
    private ConceptService conceptService;
    private Concept sampleConcept;

    @Before
    public void setUp() throws Exception {
        executeDataSet("labDataSetup.xml");
        sampleConcept = conceptService.getConcept(102);
    }

    @Test
    public void shouldPublishSampleOnSampleSave() throws Exception {
        conceptService.saveConcept(sampleConcept);
        MockHttpServletRequest request = newGetRequest("/rest/v1/reference-data/sample/" + sampleConcept.getUuid());
        MockHttpServletResponse response = handle(request);
        Sample sampleResponse = deserialize(response, Sample.class);
        assertEquals(sampleConcept.getUuid(), sampleResponse.getId());
        assertEquals(sampleConcept.getName(Context.getLocale()).getName(), sampleResponse.getShortName());
        assertEquals(sampleConcept.getName(Context.getLocale()).getName(), sampleResponse.getName());
        assertEquals(sampleConcept.isRetired(), !sampleResponse.getIsActive());
    }
}