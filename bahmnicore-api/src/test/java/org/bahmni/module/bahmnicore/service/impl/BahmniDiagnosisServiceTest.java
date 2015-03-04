package org.bahmni.module.bahmnicore.service.impl;

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
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<>(Arrays.asList(Locale.getDefault())));
    }

    @Test
    public void deleteAllGroupMembersOfDiagnosisGroup() throws Exception {
        String diagnosisEncounterUUID = "diagnosisEncounterUUID";
        String diagnosisObsUUID = "diagnosisObsUUID";

        Set<Obs> allObs = new HashSet<>();
        allObs.add(new DiagnosisBuilder().withUuid(diagnosisObsUUID).withDefaults().build());
        allObs.add(new ObsBuilder().withConcept("Some Concept", Locale.getDefault()).build());

        Encounter diagnosisEncounter = new EncounterBuilder().withDatetime(new Date()).build();
        diagnosisEncounter.setObs(allObs);

        when(encounterService.getEncounterByUuid(diagnosisEncounterUUID)).thenReturn(diagnosisEncounter);
        when(encounterService.saveEncounter(diagnosisEncounter)).thenReturn(diagnosisEncounter);


        BahmniDiagnosisServiceImpl bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(diagnosisEncounterUUID, diagnosisObsUUID);


        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService).saveEncounter(argToCapture.capture());
        Encounter encounterToSave = argToCapture.getValue();

        Obs visitDiagnosesObsToSave = getAllObsFor(encounterToSave, DiagnosisBuilder.VISIT_DIAGNOSES);
        assertTrue("Parent Diagnosis Obs should be voided", visitDiagnosesObsToSave.isVoided());
        assertFalse("Non Diagnosis Obs should not be voided", getAllObsFor(encounterToSave, "Some Concept").isVoided());
        for (Obs childObs : visitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }
    }

    @Test
    public void deleteInitialDiagnosis() throws Exception {
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
        modifiedObs.add(new DiagnosisBuilder().withUuid(modifiedDiagnosisObsUUID).withDefaults().withFirstObs(initialVisitDiagnosesObs).build());
        Encounter modifiedEncounter = new EncounterBuilder().withDatetime(new Date()).withUUID(modifiedEncounterUUID).build();
        modifiedEncounter.setObs(modifiedObs);

        when(encounterService.getEncounterByUuid(modifiedEncounterUUID)).thenReturn(modifiedEncounter);
        when(encounterService.saveEncounter(modifiedEncounter)).thenReturn(modifiedEncounter);

        BahmniDiagnosisServiceImpl bahmniDiagnosisService = new BahmniDiagnosisServiceImpl(encounterService, obsService);
        bahmniDiagnosisService.delete(modifiedEncounterUUID, modifiedDiagnosisObsUUID);


        ArgumentCaptor<Encounter> argToCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(encounterService, times(2)).saveEncounter(argToCapture.capture());
        Encounter modifiedEncounterToSave = argToCapture.getAllValues().get(0);

        Obs modifiedVisitDiagnosesObsToSave = getAllObsFor(modifiedEncounterToSave, DiagnosisBuilder.VISIT_DIAGNOSES);
        assertTrue("Parent Diagnosis Obs should be voided", modifiedVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : modifiedVisitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }

        Encounter initialEncounterToSave = argToCapture.getAllValues().get(1);

        Obs initialVisitDiagnosesObsToSave = getAllObsFor(initialEncounterToSave, DiagnosisBuilder.VISIT_DIAGNOSES);
        assertTrue("Parent Diagnosis Obs should be voided", initialVisitDiagnosesObsToSave.isVoided());
        for (Obs childObs : initialVisitDiagnosesObsToSave.getGroupMembers(true)) {
            assertTrue("Child Diagnosis Obs should be voided", childObs.isVoided());
        }
    }

    private Obs getAllObsFor(Encounter encounterToSave, String conceptName) {
        Set<Obs> allObs = encounterToSave.getAllObs(true);
        for (Obs anObs : allObs) {
            if (anObs.getConcept().getName().getName().equals(conceptName))
                return anObs;
        }
        return null;
    }
}