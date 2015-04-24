package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.test.builder.DiagnosisBuilder;
import org.bahmni.test.builder.EncounterBuilder;
import org.bahmni.test.builder.ObsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniDiagnosisMapper;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@PrepareForTest(LocaleUtility.class)
@RunWith(PowerMockRunner.class)
public class BahmniDiagnosisServiceTest {
    @Mock
    private EncounterService encounterService;
    @Mock
    private ObsService obsService;

    @Mock
    private PatientService patientService;

    private String initialDiagnosisObsUUID = "initialDiagnosisObsUUID";
    private String modifiedDiagnosisObsUUID = "modifiedDiagnosisObsUUID";
    private String initialEncounterUUID = "initialEncounterUUID";
    private Obs initialVisitDiagnosesObs;
    private Obs modifiedVisitDiagnosis;
    private Encounter initialEncounter;
    private Encounter modifiedEncounter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<>(Arrays.asList(Locale.getDefault())));
    }

    @Test
    public void deleteADiagnosis() throws Exception {
        String diagnosisObsUUID = "diagnosisObsUUID";

        Obs visitDiagnosisObs = new DiagnosisBuilder().withUuid(diagnosisObsUUID).withDefaults().withFirstObs(diagnosisObsUUID).build();
        Set<Obs> allObsForDiagnosisEncounter = new HashSet<>();
        allObsForDiagnosisEncounter.add(new DiagnosisBuilder().withUuid("someOtherDiagnosisUUID").withDefaults().withFirstObs("initialDiagnosisObsUUID").build());
        allObsForDiagnosisEncounter.add(visitDiagnosisObs);
        allObsForDiagnosisEncounter.add(new ObsBuilder().withUUID("nonDiagnosisUuid").withConcept("Some Concept", Locale.getDefault()).build());

        Encounter diagnosisEncounter = new EncounterBuilder().withDatetime(new Date()).build();
        visitDiagnosisObs.setEncounter(diagnosisEncounter);
        diagnosisEncounter.setObs(allObsForDiagnosisEncounter);

        when(obsService.getObsByUuid(diagnosisObsUUID)).thenReturn(visitDiagnosisObs);
        when(obsService.getObservationsByPersonAndConcept(visitDiagnosisObs.getPerson(), visitDiagnosisObs.getConcept())).thenReturn(Arrays.asList(visitDiagnosisObs));
        when(encounterService.saveEncounter(diagnosisEncounter)).thenReturn(diagnosisEncounter);

        BahmniDiagnosisService bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(diagnosisObsUUID);

        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService).saveEncounter(argToCapture.capture());
        assertVoided(argToCapture.getValue(), diagnosisObsUUID);
    }

    @Test
    public void initialDiagnosisIsDeletedOnDeletingADiagnosis() throws Exception {
        setUpInitialVisitDiagnosis();
        setUpModifiedVisitDiagnosis();

        when(obsService.getObsByUuid(modifiedDiagnosisObsUUID)).thenReturn(modifiedVisitDiagnosis);
        when(obsService.getObservationsByPersonAndConcept(modifiedVisitDiagnosis.getPerson(), modifiedVisitDiagnosis.getConcept())).
                thenReturn(Arrays.asList(modifiedVisitDiagnosis, initialVisitDiagnosesObs));
        when(encounterService.saveEncounter(initialEncounter)).thenReturn(initialEncounter);
        when(encounterService.saveEncounter(modifiedEncounter)).thenReturn(modifiedEncounter);

        BahmniDiagnosisServiceImpl bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(modifiedDiagnosisObsUUID);

        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService, times(2)).saveEncounter(argToCapture.capture());

        assertVoided(argToCapture.getAllValues().get(0), modifiedDiagnosisObsUUID);
        assertVoided(argToCapture.getAllValues().get(1), initialDiagnosisObsUUID);
    }

    @Test
    public void otherDiagnosisWithSameInitialDiagnosisIsDeletedOnDeletingADiagnosis() throws Exception {
        setUpInitialVisitDiagnosis();
        setUpModifiedVisitDiagnosis();
        String anotherDiagnosisUuid = "anotherDiagnosisUuid";


        Obs anotherVisitDiagnosis = new DiagnosisBuilder().withUuid(anotherDiagnosisUuid).withDefaults().withFirstObs(initialDiagnosisObsUUID).build();
        Encounter anotherEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID("anotherEncounterUuid").build();
        anotherEncounter.addObs(anotherVisitDiagnosis);
        anotherVisitDiagnosis.setEncounter(anotherEncounter);

        when(obsService.getObsByUuid(modifiedDiagnosisObsUUID)).thenReturn(modifiedVisitDiagnosis);
        when(obsService.getObservationsByPersonAndConcept(modifiedVisitDiagnosis.getPerson(), modifiedVisitDiagnosis.getConcept())).
                thenReturn(Arrays.asList(modifiedVisitDiagnosis, initialVisitDiagnosesObs, anotherVisitDiagnosis));
        when(encounterService.saveEncounter(initialEncounter)).thenReturn(initialEncounter);
        when(encounterService.saveEncounter(modifiedEncounter)).thenReturn(modifiedEncounter);

        BahmniDiagnosisService bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(modifiedDiagnosisObsUUID);

        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService, times(3)).saveEncounter(argToCapture.capture());

        assertVoided(argToCapture.getAllValues().get(0), modifiedDiagnosisObsUUID);
        assertVoided(argToCapture.getAllValues().get(1), initialDiagnosisObsUUID);
        assertVoided(argToCapture.getAllValues().get(2), anotherDiagnosisUuid);
    }

    @Test
    public void shouldGetBahmniDiagnosisByPatientAndVisit(){

        Patient patient = mock(Patient.class);
        VisitService visitService = mock(VisitService.class);
        Visit visit = mock(Visit.class);
        BahmniDiagnosisHelper bahmniDiagnosisHelper = mock(BahmniDiagnosisHelper.class);
        Concept diagnosisSetConcept = new ConceptBuilder().withUUID("uuid").build();
        Diagnosis mockDiagnosis = mock(Diagnosis.class);
        BahmniDiagnosisMapper bahmniDiagnosisMapper = mock(BahmniDiagnosisMapper.class);
        DiagnosisMapper diagnosisMapper = mock(DiagnosisMapper.class);

        EmrApiProperties properties = mock(EmrApiProperties.class, RETURNS_DEEP_STUBS);
        when(properties.getDiagnosisMetadata().getDiagnosisSetConcept()).thenReturn(diagnosisSetConcept);


        when(visitService.getVisitByUuid("visitId")).thenReturn(visit);
        when(visit.getEncounters()).thenReturn(new HashSet<Encounter>());
        when(patientService.getPatientByUuid("patientId")).thenReturn(patient);

        Obs diagnosisObs = new DiagnosisBuilder()
                .withDefaults()
                .withFirstObs("firstDiagnosisObsId")
                .withUuid("firstDiagnosisObsId")
                .build();


        when(obsService.getObservations(eq(Arrays.asList((Person) patient)), anyList(), eq(Arrays.asList(diagnosisSetConcept)), anyListOf(Concept.class), anyList(), anyList(), anyList(),
                anyInt(), anyInt(), Matchers.any(java.util.Date.class), Matchers.any(java.util.Date.class), eq(false)))
                .thenReturn(Arrays.asList(diagnosisObs));
        when(bahmniDiagnosisHelper.buildDiagnosisFromObsGroup(diagnosisObs)).thenReturn(mockDiagnosis);
        EncounterTransaction.Diagnosis etDiagnosis = mock(EncounterTransaction.Diagnosis.class);

        Diagnosis latestDiagnosis = mock(Diagnosis.class);
        EncounterTransaction.Diagnosis etLatestDiagnosis = mock(EncounterTransaction.Diagnosis.class);
        when(diagnosisMapper.convert(mockDiagnosis)).thenReturn(etDiagnosis);
        when(bahmniDiagnosisHelper.getLatestBasedOnAnyDiagnosis(mockDiagnosis)).thenReturn(latestDiagnosis);
        when(diagnosisMapper.convert(latestDiagnosis)).thenReturn(etLatestDiagnosis);

        BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setExistingObs("existing");
        bahmniDiagnosisRequest.setFirstDiagnosis(bahmniDiagnosis);
        when(bahmniDiagnosisMapper.mapBahmniDiagnosis(etDiagnosis, etLatestDiagnosis, true, false)).thenReturn(bahmniDiagnosisRequest);

        BahmniDiagnosisServiceImpl bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.setEmrApiProperties(properties);
        bahmniDiagnosisService.setPatientService(patientService);
        bahmniDiagnosisService.setVisitService(visitService);
        bahmniDiagnosisService.setBahmniDiagnosisHelper(bahmniDiagnosisHelper);
        bahmniDiagnosisService.setDiagnosisMapper(diagnosisMapper);
        bahmniDiagnosisService.setBahmniDiagnosisMapper(bahmniDiagnosisMapper);

        List<BahmniDiagnosisRequest> bahmniDiagnosisRequests = bahmniDiagnosisService.getBahmniDiagnosisByPatientAndVisit("patientId", "visitId");
        assertEquals(1,bahmniDiagnosisRequests.size());
        assertEquals(bahmniDiagnosisRequest,bahmniDiagnosisRequests.get(0));
    }

    private void setUpModifiedVisitDiagnosis() {
        modifiedVisitDiagnosis = new DiagnosisBuilder().withUuid(modifiedDiagnosisObsUUID).withDefaults().withFirstObs(initialDiagnosisObsUUID).build();
        modifiedEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID("modifiedEncounterUUID").build();
        modifiedEncounter.addObs(modifiedVisitDiagnosis);
        modifiedVisitDiagnosis.setEncounter(modifiedEncounter);
    }

    private void setUpInitialVisitDiagnosis() {
        initialVisitDiagnosesObs = new DiagnosisBuilder().withUuid(initialDiagnosisObsUUID).withDefaults().withFirstObs(initialDiagnosisObsUUID).build();
        initialEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(initialEncounterUUID).build();
        initialEncounter.addObs(initialVisitDiagnosesObs);
        initialVisitDiagnosesObs.setEncounter(initialEncounter);
    }


    private void assertVoided(Encounter encounter, String observationUuid) {
        Obs visitDiagnosesObsToSave = getAllObsFor(encounter, observationUuid);
        assertTrue("Parent Diagnosis Obs should be voided", visitDiagnosesObsToSave.isVoided());
        for (Obs childObs : visitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }
    }

    private Obs getAllObsFor(Encounter encounterToSave, String visitDiagnosisUuid) {
        Set<Obs> allObs = encounterToSave.getAllObs(true);
        for (Obs anObs : allObs) {
            if (anObs.getUuid().equals(visitDiagnosisUuid))
                return anObs;
        }
        return null;
    }
}