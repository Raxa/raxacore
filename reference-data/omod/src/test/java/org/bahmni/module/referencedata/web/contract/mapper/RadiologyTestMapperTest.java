package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.*;
import org.bahmni.module.referencedata.labconcepts.mapper.RadiologyTestMapper;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.*;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.bahmni.module.referencedata.labconcepts.advice.ConceptServiceEventInterceptorTest.createConceptSet;
import static org.bahmni.module.referencedata.labconcepts.advice.ConceptServiceEventInterceptorTest.getConceptSets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class RadiologyTestMapperTest {
    private RadiologyTestMapper testMapper;
    private Concept radiologyConcept;
    private Date dateCreated;
    private Date dateChanged;
    @Mock
    private ConceptService conceptService;
    private ConceptSet testRadiologyTestConceptSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testMapper = new RadiologyTestMapper();
        dateCreated = new Date();
        dateChanged = new Date();
        Locale defaultLocale = new Locale("en", "GB");
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(defaultLocale);

       radiologyConcept = new ConceptBuilder().withUUID("RadiologyUUID").withDateCreated(dateCreated).withClass(RadiologyTest.RADIOLOGY_TEST_CONCEPT_CLASS).
                withDateChanged(dateChanged).withShortName("clavicle - right, 2 views (x-ray)").withName("Clavicle - Right, 2 views (X-ray)").build();

        when(Context.getConceptService()).thenReturn(conceptService);
    }

    @Test
    public void map_name_of_radiology_test_from_concept() throws Exception {
        RadiologyTest testData = testMapper.map(radiologyConcept);
        assertEquals("RadiologyUUID", testData.getId());
        assertEquals(dateCreated, testData.getDateCreated());
        assertEquals(dateChanged, testData.getLastUpdated());
        assertEquals("Clavicle - Right, 2 views (X-ray)", testData.getName());
    }


}