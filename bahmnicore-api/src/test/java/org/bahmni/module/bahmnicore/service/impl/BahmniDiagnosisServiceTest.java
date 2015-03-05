package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.bahmni.test.builder.DiagnosisBuilder;
import org.bahmni.test.builder.EncounterBuilder;
import org.bahmni.test.builder.ObsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<>(asList(Locale.getDefault())));
    }

    @Test
    public void deleteADiagnosis() throws Exception {
        String diagnosisEncounterUUID = "diagnosisEncounterUUID";
        String diagnosisObsUUID = "diagnosisObsUUID";

        Obs initialDiagnosisForOther = new DiagnosisBuilder().withUuid("initialDiagnosisObsUUID").withDefaults().build();
        Encounter initialOtherEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID("initialEncounterUUID").build();
        initialOtherEncounter.addObs(initialDiagnosisForOther);

        Obs visitDiagnosisObs = new DiagnosisBuilder().withUuid(diagnosisObsUUID).withDefaults().build();
        Set<Obs> allObsForDiagnosisEncounter = new HashSet<>();
        allObsForDiagnosisEncounter.add(new DiagnosisBuilder().withUuid("someOtherDiagnosisUUID").withDefaults().withFirstObs(initialDiagnosisForOther).build());
        allObsForDiagnosisEncounter.add(visitDiagnosisObs);
        allObsForDiagnosisEncounter.add(new ObsBuilder().withUUID("nonDiagnosisUuid").withConcept("Some Concept", Locale.getDefault()).build());

        when(obsService.getObsByUuid(diagnosisObsUUID)).thenReturn(visitDiagnosisObs);

        Encounter diagnosisEncounter = new EncounterBuilder().withDatetime(new Date()).build();
        diagnosisEncounter.setObs(allObsForDiagnosisEncounter);
        when(encounterService.getEncounterByUuid(diagnosisEncounterUUID)).thenReturn(diagnosisEncounter);
        when(encounterService.saveEncounter(diagnosisEncounter)).thenReturn(diagnosisEncounter);

        BahmniDiagnosisService bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(diagnosisEncounterUUID, diagnosisObsUUID);

        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService).saveEncounter(argToCapture.capture());
        Encounter encounterToSave = argToCapture.getValue();

        Obs visitDiagnosesObsToSave = getAllObsFor(encounterToSave, diagnosisObsUUID);
        assertTrue("Parent Diagnosis Obs should be voided", visitDiagnosesObsToSave.isVoided());
        assertFalse("Non Diagnosis Obs should not be voided", getAllObsFor(encounterToSave, "nonDiagnosisUuid").isVoided());
        for (Obs childObs : visitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }
    }

    @Test
    public void initialDiagnosisIsDeletedOnDeletingADiagnosis() throws Exception {
        String initialDiagnosisObsUUID = "initialDiagnosisObsUUID";
        String modifiedDiagnosisObsUUID = "modifiedDiagnosisObsUUID";
        String modifiedEncounterUUID = "modifiedEncounterUUID";
        String initialEncounterUUID = "initialEncounterUUID";

        Obs initialVisitDiagnosesObs = new DiagnosisBuilder().withUuid(initialDiagnosisObsUUID).withDefaults().build();
        Encounter initialEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(initialEncounterUUID).build();
        initialEncounter.addObs(initialVisitDiagnosesObs);

        when(obsService.getObsByUuid(initialDiagnosisObsUUID)).thenReturn(initialVisitDiagnosesObs);
        when(encounterService.saveEncounter(initialEncounter)).thenReturn(initialEncounter);


        Set<Obs> modifiedObs = new HashSet<>();
        Obs modifiedVisitDiagnosis = new DiagnosisBuilder().withUuid(modifiedDiagnosisObsUUID).withDefaults().withFirstObs(initialVisitDiagnosesObs).build();
        modifiedObs.add(modifiedVisitDiagnosis);
        Encounter modifiedEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(modifiedEncounterUUID).build();
        modifiedEncounter.setObs(modifiedObs);
        when(obsService.getObsByUuid(modifiedDiagnosisObsUUID)).thenReturn(modifiedVisitDiagnosis);

        when(encounterService.getEncounterByUuid(modifiedEncounterUUID)).thenReturn(modifiedEncounter);
        when(encounterService.saveEncounter(modifiedEncounter)).thenReturn(modifiedEncounter);

        BahmniDiagnosisService bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(modifiedEncounterUUID, modifiedDiagnosisObsUUID);


        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService, times(2)).saveEncounter(argToCapture.capture());
        Encounter initialEncounterToSave = argToCapture.getAllValues().get(0);

        Obs modifiedVisitDiagnosesObsToSave = getAllObsFor(initialEncounterToSave, initialDiagnosisObsUUID);
        assertTrue("Parent Diagnosis Obs should be voided", modifiedVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : modifiedVisitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }

        Encounter modifiedEncounterToSave = argToCapture.getAllValues().get(1);

        Obs initialVisitDiagnosesObsToSave = getAllObsFor(modifiedEncounterToSave, modifiedDiagnosisObsUUID);
        assertTrue("Parent Diagnosis Obs should be voided", initialVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : initialVisitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }
    }

    @Test
    public void otherDiagnosisWithSameInitialDiagnosisIsDeletedOnDeletingADiagnosis() throws Exception {
        String initialDiagnosisObsUUID = "initialDiagnosisObsUUID";
        String modifiedDiagnosisObsUUID = "modifiedDiagnosisObsUUID";
        String modifiedEncounterUUID = "modifiedEncounterUUID";
        String initialEncounterUUID = "initialEncounterUUID";

        Obs initialVisitDiagnosesObs = new DiagnosisBuilder().withUuid(initialDiagnosisObsUUID).withDefaults().build();
        Encounter initialEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(initialEncounterUUID).build();
        initialEncounter.addObs(initialVisitDiagnosesObs);

        when(obsService.getObsByUuid(initialDiagnosisObsUUID)).thenReturn(initialVisitDiagnosesObs);
        when(encounterService.saveEncounter(initialEncounter)).thenReturn(initialEncounter);


        Set<Obs> modifiedObs = new HashSet<>();
        Obs modifiedVisitDiagnosis = new DiagnosisBuilder().withUuid(modifiedDiagnosisObsUUID).withDefaults().withFirstObs(initialVisitDiagnosesObs).build();
        modifiedObs.add(modifiedVisitDiagnosis);
        Encounter modifiedEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(modifiedEncounterUUID).build();
        modifiedEncounter.setObs(modifiedObs);

        Set<Obs> anotherObs = new HashSet<>();
        Obs anotherVisitDiagnosis = new DiagnosisBuilder().withUuid("anotherDiagnosisUuid").withDefaults().withFirstObs(initialVisitDiagnosesObs).build();
        anotherObs.add(anotherVisitDiagnosis);
        Encounter anotherEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID("anotherEncounterUuid").build();
        anotherVisitDiagnosis.setEncounter(anotherEncounter);
        anotherEncounter.setObs(anotherObs);


        modifiedEncounter.setObs(anotherObs);
        when(obsService.getObsByUuid(modifiedDiagnosisObsUUID)).thenReturn(anotherVisitDiagnosis);

        when(obsService.getObservations(anyList(), anyList(), anyList(),
                anyList(), anyList(), anyList(), anyList(), anyInt(), anyInt(), (Date)anyObject(), (Date)any(), eq(false))).thenReturn(asList(anotherVisitDiagnosis));

        when(encounterService.getEncounterByUuid(modifiedEncounterUUID)).thenReturn(modifiedEncounter);
        when(encounterService.saveEncounter(modifiedEncounter)).thenReturn(modifiedEncounter);

        BahmniDiagnosisService bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(modifiedEncounterUUID, modifiedDiagnosisObsUUID);


        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService, times(3)).saveEncounter(argToCapture.capture());
        Encounter initialEncounterToSave = argToCapture.getAllValues().get(0);

        Obs modifiedVisitDiagnosesObsToSave = getAllObsFor(initialEncounterToSave, initialDiagnosisObsUUID);
        assertTrue("Parent Diagnosis Obs should be voided", modifiedVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : modifiedVisitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }

        Encounter modifiedEncounterToSave = argToCapture.getAllValues().get(1);

        Obs initialVisitDiagnosesObsToSave = getAllObsFor(modifiedEncounterToSave, modifiedDiagnosisObsUUID);
        assertTrue("Parent Diagnosis Obs should be voided", initialVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : initialVisitDiagnosesObsToSave.getGroupMembers(true)) {
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