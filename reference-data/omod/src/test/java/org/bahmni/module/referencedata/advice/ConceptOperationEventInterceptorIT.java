//TODO : MIHIR : Add more ITs for tests and panels only happy path will be fine, and resolve all the TODOS in the Bahmni-Core
package org.bahmni.module.referencedata.advice;

import org.bahmni.module.bahmnicore.web.v1_0.controller.BaseWebControllerTest;
import org.bahmni.module.referencedata.web.contract.Department;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ConceptOperationEventInterceptorIT extends BaseWebControllerTest {
    @Autowired
    private ConceptService conceptService;
    private Concept sampleConcept;
    private Concept departmentConcept;

    @Before
    public void setUp() throws Exception {
        executeDataSet("labDataSetup.xml");
        sampleConcept = conceptService.getConcept(102);
        departmentConcept = conceptService.getConcept(202);
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
        assertNotEquals(sampleConcept.isRetired(), sampleResponse.getIsActive());
    }


    @Test
    public void shouldPublishDepartmentOnDepartmentSave() throws Exception {
        conceptService.saveConcept(departmentConcept);
        MockHttpServletRequest request = newGetRequest("/rest/v1/reference-data/department/" + departmentConcept.getUuid());
        MockHttpServletResponse response = handle(request);
        Department departmentResponse = deserialize(response, Department.class);
        assertEquals(departmentConcept.getUuid(), departmentResponse.getId());
        assertNull(departmentResponse.getDescription());
        assertEquals(departmentConcept.getName(Context.getLocale()).getName(), departmentResponse.getName());
        assertNotEquals(departmentConcept.isRetired(), departmentResponse.getIsActive());
    }
}