package org.bahmni.module.bahmnicore.web.v1_0.controller.display.controls;

import org.bahmni.module.admin.retrospectiveEncounter.domain.DuplicateObservationsMatcher;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.BahmniObservationsToTabularViewMapper;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.pivottable.contract.PivotTable;
import org.openmrs.module.emrapi.encounter.ConceptMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DuplicateObservationsMatcher.class, LocaleUtility.class})
public class ObsToObsTabularFlowSheetControllerTest {

    @Mock
    private AdministrationService adminService;
    @Mock
    private BahmniObservationsToTabularViewMapper bahmniObservationsToTabularViewMapper;
    @Mock
    private ConceptService conceptService;
    @Mock
    private BahmniObsService bahmniObsService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ObsToObsTabularFlowSheetController obsToObsPivotTableController;
    private ConceptMapper conceptMapper = new ConceptMapper();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);
        Context.setUserContext(new UserContext());
        obsToObsPivotTableController = new ObsToObsTabularFlowSheetController(bahmniObsService, conceptService, bahmniObservationsToTabularViewMapper, null);
    }

    @Test
    public void shouldFetchObservationForSpecifiedConceptsAndGroupByConcept() {
        Concept member1 = new ConceptBuilder().withName("Member1").withDataType("Numeric").build();
        Concept member2 = new ConceptBuilder().withName("Member2").withDataType("Numeric").build();
        Concept groupByConcept = new ConceptBuilder().withName("GroupByConcept").withDataType("Numeric").build();
        Concept rootConcept = new ConceptBuilder().withName("ConceptSetName").withSetMember(groupByConcept).withSetMember(member1).withSetMember(member2).withDataType("Numeric").build();
        when(conceptService.getConceptByName("ConceptSetName")).thenReturn(rootConcept);
        when(conceptService.getConceptByName("GroupByConcept")).thenReturn(groupByConcept);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        when(bahmniObsService.observationsFor("patientUuid", rootConcept, groupByConcept, 1)).thenReturn(bahmniObservations);

        PivotTable pivotTable = new PivotTable();
        List<String> conceptNames = Arrays.asList("Member1", "Member2");
        Set<EncounterTransaction.Concept> leafConcepts = new HashSet<>(Arrays.asList(conceptMapper.map(member1), conceptMapper.map(member2), conceptMapper.map(groupByConcept)));
        when(bahmniObservationsToTabularViewMapper.constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations))).thenReturn(pivotTable);

        PivotTable actualPivotTable = obsToObsPivotTableController.constructPivotTableFor("patientUuid", 1, "ConceptSetName", "GroupByConcept", conceptNames);

        verify(conceptService, times(1)).getConceptByName("ConceptSetName");
        verify(bahmniObsService, times(1)).observationsFor("patientUuid", rootConcept, groupByConcept, 1);
        verify(bahmniObservationsToTabularViewMapper, times(1)).constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations));
        assertNotNull(actualPivotTable);
        assertEquals(pivotTable, actualPivotTable);
    }

    @Test
    public void shouldFetchAllVisitsDataIfNumberOfVisitsIsPassedAsNull() throws Exception {
        Concept groupByConcept = new ConceptBuilder().withName("GroupByConcept").withDataType("Numeric").build();
        Concept rootConcept = new ConceptBuilder().withName("ConceptSetName").withSetMember(groupByConcept).withDataType("Numeric").build();
        when(conceptService.getConceptByName("ConceptSetName")).thenReturn(rootConcept);
        when(conceptService.getConceptByName("GroupByConcept")).thenReturn(groupByConcept);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        when(bahmniObsService.observationsFor("patientUuid", rootConcept, groupByConcept, null)).thenReturn(bahmniObservations);

        PivotTable pivotTable = new PivotTable();
        List<String> conceptNames = Arrays.asList("GroupByConcept");
        Set<String> leafConcepts = new HashSet<>(Arrays.asList("GroupByConcept"));

        when(bahmniObservationsToTabularViewMapper.constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations))).thenReturn(pivotTable);

        PivotTable actualPivotTable = obsToObsPivotTableController.constructPivotTableFor("patientUuid", null, "ConceptSetName", "GroupByConcept", conceptNames);

        verify(conceptService, times(1)).getConceptByName("ConceptSetName");
        verify(bahmniObsService, times(1)).observationsFor("patientUuid", rootConcept, groupByConcept, null);
        verify(bahmniObservationsToTabularViewMapper, times(1)).constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations));
        assertNotNull(actualPivotTable);
        assertEquals(pivotTable, actualPivotTable);
    }

    @Test
    public void shouldFetchAllVisitsDataIfNumberOfVisitsIsPassedAsZero() throws Exception {
        Concept groupByConcept = new ConceptBuilder().withName("GroupByConcept").withDataType("Numeric").build();
        Concept rootConcept = new ConceptBuilder().withName("ConceptSetName").withDataType("Numeric").withSetMember(groupByConcept).build();
        when(conceptService.getConceptByName("ConceptSetName")).thenReturn(rootConcept);
        when(conceptService.getConceptByName("GroupByConcept")).thenReturn(groupByConcept);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        when(bahmniObsService.observationsFor("patientUuid", rootConcept, groupByConcept, 0)).thenReturn(bahmniObservations);

        PivotTable pivotTable = new PivotTable();
        when(bahmniObservationsToTabularViewMapper.constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations))).thenReturn(pivotTable);

        PivotTable actualPivotTable = obsToObsPivotTableController.constructPivotTableFor("patientUuid", 0, "ConceptSetName", "GroupByConcept", Collections.EMPTY_LIST);

        verify(conceptService, times(1)).getConceptByName("ConceptSetName");
        verify(bahmniObsService, times(1)).observationsFor("patientUuid", rootConcept, groupByConcept, 0);
        verify(bahmniObservationsToTabularViewMapper, times(1)).constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations));
        assertNotNull(actualPivotTable);
        assertEquals(pivotTable, actualPivotTable);
    }

    @Test
    public void shouldFetchAllVisitsDataIfNumberOfVisitsIsPassedAsNegative() throws Exception {
        Concept groupByConcept = new ConceptBuilder().withName("GroupByConcept").withClass("N/A").withDataType("Numeric").build();
        Concept rootConcept = new ConceptBuilder().withName("ConceptSetName").withClass("N/A").withSetMember(groupByConcept).withDataType("Numeric").build();
        when(conceptService.getConceptByName("ConceptSetName")).thenReturn(rootConcept);
        when(conceptService.getConceptByName("GroupByConcept")).thenReturn(groupByConcept);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        when(bahmniObsService.observationsFor("patientUuid", rootConcept, groupByConcept, -1)).thenReturn(bahmniObservations);

        PivotTable pivotTable = new PivotTable();
        Set<String> leafConcepts = new HashSet<>(Arrays.asList("GroupByConcept"));
        when(bahmniObservationsToTabularViewMapper.constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations))).thenReturn(pivotTable);

        PivotTable actualPivotTable = obsToObsPivotTableController.constructPivotTableFor("patientUuid", -1, "ConceptSetName", "GroupByConcept", Collections.EMPTY_LIST);

        verify(conceptService, times(1)).getConceptByName("ConceptSetName");
        verify(bahmniObsService, times(1)).observationsFor("patientUuid", rootConcept, groupByConcept, -1);
        verify(bahmniObservationsToTabularViewMapper, times(1)).constructTable(Matchers.<Set<EncounterTransaction.Concept>>any(), eq(bahmniObservations));
        assertNotNull(actualPivotTable);
        assertEquals(pivotTable, actualPivotTable);
    }

    @Test
    public void shouldThrowExceptionIfConceptSetNotFound() {
        String conceptSetName = "ConceptSetName";
        when(conceptService.getConceptByName(conceptSetName)).thenReturn(null);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Root concept not found for the name:  " + conceptSetName);

        obsToObsPivotTableController.constructPivotTableFor("patientUuid", 1, conceptSetName, "GroupByConcept", Collections.EMPTY_LIST);
    }

    @Test
    public void shouldThrowExceptionIfGroupByConceptIsNotProvided() {
        String conceptSetName = "ConceptSetName";
        Concept conceptSet = new ConceptBuilder().withName(conceptSetName).withSetMember(new ConceptBuilder().withName("GroupByConcept").build()).build();
        when(conceptService.getConceptByName(conceptSetName)).thenReturn(conceptSet);
        exception.expect(RuntimeException.class);
        exception.expectMessage("null doesn't belong to the Root concept:  " + conceptSetName);

        obsToObsPivotTableController.constructPivotTableFor("patientUuid", 1, conceptSetName, null, Collections.EMPTY_LIST);
    }

    @Test
    public void shouldThrowExceptionIfGroupByConceptDoesNotBelongToConceptSet() {
        String conceptSetName = "ConceptSetName";
        Concept conceptSet = new ConceptBuilder().withName(conceptSetName).withSetMember(new ConceptBuilder().withName("NotGroupByConcept").build()).build();
        when(conceptService.getConceptByName(conceptSetName)).thenReturn(conceptSet);
        exception.expect(RuntimeException.class);
        exception.expectMessage("GroupByConcept doesn't belong to the Root concept:  " + conceptSetName);

        obsToObsPivotTableController.constructPivotTableFor("patientUuid", 1, conceptSetName, "GroupByConcept", Collections.EMPTY_LIST);
    }

}