package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.builder.ConceptBuilder;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@PrepareForTest({Context.class,LocaleUtility.class})
@RunWith(PowerMockRunner.class)
public class BahmniDiagnosisMetadataTest {
    @Mock
    private ObsService obsService;
    @Mock
    private ConceptService conceptService;

    @Mock(answer= Answers.RETURNS_DEEP_STUBS)
    private EmrApiProperties properties;

    public static final String BOOLEAN_UUID = "8d4a5cca-c2cc-11de-8d13-0010c6dffd0f";
    private static final String BAHMNI_DIAGNOSIS_STATUS = "Bahmni Diagnosis Status";
    private static final String BAHMNI_DIAGNOSIS_REVISED = "Bahmni Diagnosis Revised";
    private static final String BAHMNI_INITIAL_DIAGNOSIS = "Bahmni Initial Diagnosis";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotAddStatusAsGroupMemberIfStatusIsNotSpecified() throws Exception {

        BahmniDiagnosisMetadata bahmniDiagnosisMetadata = new BahmniDiagnosisMetadata(obsService, conceptService,properties, null);
        when(conceptService.getConceptByName(BAHMNI_DIAGNOSIS_STATUS)).thenReturn(new Concept());
        Obs diagnosisObs = new Obs();
        diagnosisObs.setGroupMembers(Collections.emptySet());
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setDiagnosisStatusConcept(null);

        bahmniDiagnosisMetadata.updateStatusConcept(diagnosisObs, bahmniDiagnosis);

        assertEquals(0, diagnosisObs.getGroupMembers().size());

    }

    @Test
    public void shouldAddStatusAsGroupMemberIfStatusIsSpecified() throws Exception {
        BahmniDiagnosisMetadata bahmniDiagnosisMetadata = new BahmniDiagnosisMetadata(obsService, conceptService,properties, null);
        when(conceptService.getConceptByName(BAHMNI_DIAGNOSIS_STATUS)).thenReturn(new Concept());
        Obs diagnosisObs = new Obs();
        diagnosisObs.setGroupMembers(Collections.emptySet());
        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        Concept inactiveStatus = new ConceptBuilder().withName("Inactive").build();
        when(conceptService.getConcept("Inactive")).thenReturn(inactiveStatus);
        EncounterTransaction.Concept etInactiveStatusConcept = new EncounterTransaction.Concept();
        etInactiveStatusConcept.setName("Inactive");
        bahmniDiagnosis.setDiagnosisStatusConcept(etInactiveStatusConcept);

        bahmniDiagnosisMetadata.updateStatusConcept(diagnosisObs, bahmniDiagnosis);

        assertEquals(1, diagnosisObs.getGroupMembers().size());
    }
}
