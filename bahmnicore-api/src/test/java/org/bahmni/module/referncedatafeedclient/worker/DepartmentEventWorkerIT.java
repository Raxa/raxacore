package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.client.AtomFeedProcessor;
import org.bahmni.module.referncedatafeedclient.domain.Department;
import org.bahmni.module.referncedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.module.referncedatafeedclient.worker.DepartmentEventWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.atomfeed.common.repository.OpenMRSJdbcConnectionProvider;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class DepartmentEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private final String referenceDataUri = "http://localhost";

    @Autowired
    private ConceptService conceptService;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    private DepartmentEventWorker departmentEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        departmentEventWorker = new DepartmentEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("departmentEventWorkerTestData.xml");
    }

    @Test
    public void shouldCreateNewConceptForGivenDepartment() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/department/8471dbe5-0465-4eac-94ba-8f8708f3f529");
        Department department = new Department("8471dbe5-0465-4eac-94ba-8f8708f3f529", "BioChem", "BioChem Dep");
        when(httpClient.get(referenceDataUri + event.getContent(), Department.class)).thenReturn(department);

        departmentEventWorker.process(event);

        Concept departmentConcept = conceptService.getConceptByUuid(department.getId());
        assertNotNull(departmentConcept);
        assertEquals(1, departmentConcept.getNames().size());
        assertEquals("BioChem Department", departmentConcept.getName(Locale.ENGLISH).getName());
        assertEquals(1, departmentConcept.getDescriptions().size());
        assertEquals(department.getDescription(), departmentConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, departmentConcept.getDatatype().getUuid());
        assertEquals(DepartmentEventWorker.CONV_SET, departmentConcept.getConceptClass().getName());
        assertEquals(true, departmentConcept.isSet());
        Concept labDepartmentsConcept = conceptService.getConceptByName(DepartmentEventWorker.LAB_DEPARTMENTS);
        assertTrue(labDepartmentsConcept.getSetMembers().contains(departmentConcept));
    }

    @Test
    public void shouldUpdateConceptForGivenDepartment() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/department/dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department department = new Department("dc8ac8c0-8716-11e3-baa7-0800200c9a66", "Haematology updated", "Haematology Description updated");
        when(httpClient.get(referenceDataUri+event.getContent(), Department.class)).thenReturn(department);

        departmentEventWorker.process(event);

        Concept departmentConcept = conceptService.getConceptByUuid(department.getId());
        assertNotNull(departmentConcept);
        assertEquals(1, departmentConcept.getNames().size());
        assertEquals("Haematology updated Department",departmentConcept.getName(Locale.ENGLISH).getName());
        assertEquals(1, departmentConcept.getDescriptions().size());
        assertEquals(department.getDescription(), departmentConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, departmentConcept.getDatatype().getUuid());
        assertEquals(DepartmentEventWorker.CONV_SET, departmentConcept.getConceptClass().getName());
        assertEquals(true, departmentConcept.isSet());
        Concept labDepartmentsConcept = conceptService.getConceptByName(DepartmentEventWorker.LAB_DEPARTMENTS);
        assertTrue(labDepartmentsConcept.getSetMembers().contains(departmentConcept));
    }
}
