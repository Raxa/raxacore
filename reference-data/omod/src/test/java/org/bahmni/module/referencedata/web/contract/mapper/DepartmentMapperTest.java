package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Department;
import org.bahmni.module.referencedata.labconcepts.mapper.DepartmentMapper;
import org.bahmni.module.referencedata.labconcepts.mapper.ResourceMapper;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.bahmni.module.referencedata.labconcepts.advice.ConceptOperationEventInterceptorTest.getConceptSet;
import static org.bahmni.module.referencedata.labconcepts.advice.ConceptOperationEventInterceptorTest.getConceptSets;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class DepartmentMapperTest {

    private DepartmentMapper departmentMapper;
    private Concept departmentConcept;
    private Date dateCreated;
    private Date dateChanged;
    private Concept labDepartmentConcept;
    @Mock
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        departmentMapper = new DepartmentMapper();
        dateCreated = new Date();
        dateChanged = new Date();
        Locale defaultLocale = new Locale("en", "GB");
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(defaultLocale);
        departmentConcept = new ConceptBuilder().withUUID("Department UUID").withDateCreated(dateCreated).
                withDateChanged(dateChanged).withDescription("Some Description").withName("SampleName").build();
        labDepartmentConcept = new ConceptBuilder().withUUID("Laboratory UUID")
                .withName(Department.DEPARTMENT_PARENT_CONCEPT_NAME).withClass(Department.DEPARTMENT_CONCEPT_CLASS)
                .withSetMember(departmentConcept).build();
        ConceptSet conceptSet = getConceptSet(labDepartmentConcept, departmentConcept);
        List<ConceptSet> conceptSets = getConceptSets(conceptSet);
        when(conceptService.getSetsContainingConcept(any(Concept.class))).thenReturn(conceptSets);
        when(Context.getConceptService()).thenReturn(conceptService);
    }

    @Test
    public void map_all_sample_fields_from_concept() throws Exception {
        Department departmentData = departmentMapper.map(departmentConcept);
        assertEquals("Department UUID", departmentData.getId());
        assertEquals(dateCreated, departmentData.getDateCreated());
        assertEquals(dateChanged, departmentData.getLastUpdated());
        assertEquals("Some Description", departmentData.getDescription());
    }

    @Test
    public void map_if_no_parent_concept() throws Exception {
        Concept departmentConcept = new ConceptBuilder().withUUID("Department UUID").withDateCreated(dateCreated).
                withDateChanged(dateChanged).withName("DepartmentName").build();
        Department departmentData = departmentMapper.map(departmentConcept);
        assertEquals("Department UUID", departmentData.getId());
    }

    @Test
    public void is_active_true_by_default() throws Exception {
        Department departmentData = departmentMapper.map(departmentConcept);
        assertTrue(departmentData.getIsActive());
    }

    @Test
    public void should_set_name_if_description_is_null() throws Exception {

        Concept departmentConceptWithOutDescription = new ConceptBuilder().withUUID("Department UUID").withDateCreated(dateCreated).
                withDateChanged(dateChanged).withName("DepartmentName").build();
        Department departmentData = departmentMapper.map(departmentConceptWithOutDescription);
        assertEquals("DepartmentName", departmentData.getDescription());
    }
}