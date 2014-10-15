package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.AllTestsAndPanels;
import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.bahmni.module.referencedata.labconcepts.contract.Panel;
import org.bahmni.module.referencedata.labconcepts.mapper.AllTestsAndPanelsMapper;
import org.bahmni.module.referencedata.labconcepts.mapper.LabTestMapper;
import org.bahmni.module.referencedata.labconcepts.mapper.PanelMapper;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.powermock.api.mockito.PowerMockito;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.assertEquals;


import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.Locale;

import java.util.Date;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class AllTestsAndPanelsMapperTest {
    private AllTestsAndPanelsMapper allTestsAndPanelsMapper;
    private LabTestMapper testMapper;
    private Date dateChanged;
    private Date dateCreated;
    private Concept testConcept;
    private Concept panelConcept;
    private Concept testAndPanelsConcept;
    @Mock
    private ConceptService conceptService;
    private PanelMapper panelMapper;

    @Before
    public void setUp() throws Exception {
        allTestsAndPanelsMapper = new AllTestsAndPanelsMapper();
        testMapper = new LabTestMapper();
        panelMapper = new PanelMapper();
        dateCreated = new Date();
        dateChanged = new Date();
        Locale defaultLocale = new Locale("en", "GB");
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(defaultLocale);

        testConcept = new ConceptBuilder().withUUID("Test UUID").withDateCreated(dateCreated).withClassUUID(ConceptClass.TEST_UUID).withDescription("SomeDescription")
                .withDateChanged(dateChanged).withShortName("ShortName").withName("Test concept").withDataType(ConceptDatatype.NUMERIC).build();

        panelConcept = new ConceptBuilder().withUUID("Panel UUID").withDateCreated(dateCreated).withClassUUID(ConceptClass.LABSET_UUID).withDescription("SomeDescription")
                .withSetMember(testConcept).withDateChanged(dateChanged).withShortName("ShortName").withName("Panel Name").withDataType(ConceptDatatype.NUMERIC).build();
        testAndPanelsConcept = new ConceptBuilder().withUUID("Test and Panels UUID").withDateCreated(dateCreated).withClassUUID(ConceptClass.CONVSET_UUID).withDescription("Test and Panel Description")
                .withDateChanged(dateChanged).withShortName("ShortName").withName(AllTestsAndPanels.ALL_TESTS_AND_PANELS).withSetMember(testConcept).withSetMember(panelConcept).build();

        when(Context.getConceptService()).thenReturn(conceptService);
    }

    @Test
    public void map_all_Tests_And_Panels_fields_from_concept() throws Exception {

        AllTestsAndPanels testsAndPanels = allTestsAndPanelsMapper.map(testAndPanelsConcept);
        LabTest testData = testMapper.map(testConcept);
        Panel panelData = panelMapper.map(panelConcept);

        assertEquals("Test and Panels UUID", testsAndPanels.getId());
        assertEquals("All_Tests_and_Panels", testsAndPanels.getName());
        assertEquals(dateCreated, testsAndPanels.getDateCreated());
        assertEquals(dateChanged, testsAndPanels.getLastUpdated());
        assertEquals("Test and Panel Description", testsAndPanels.getDescription());

        List<LabTest> tests = testsAndPanels.getTests();
        assertEquals(1, tests.size());
        assertEquals(testData.getId(), tests.get(0).getId());

        List<Panel> panels = testsAndPanels.getPanels();
        assertEquals(1, panels.size());
        assertEquals(panelData.getId(), panels.get(0).getId());
    }
}