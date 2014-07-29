package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class BahmniDiagnosisHelperTest {
    @Mock
    private ObsService obsService;
    @Mock
    private ConceptService conceptService;

    public static final String BOOLEAN_UUID = "8d4a5cca-c2cc-11de-8d13-0010c6dffd0f";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUpdateComments() {
        String comments = "High fever and condition implies Pneumonia";
        BahmniDiagnosisRequest bahmniDiagnosis = new BahmniDiagnosisRequest();
        bahmniDiagnosis.setComments(comments);

        Encounter encounter = new Encounter(){{
            this.addObs(new Obs(){{
                setUuid("Diagnosis-Uuid");
                addGroupMember(new Obs());
            }});
        }};

        EncounterTransaction.Diagnosis diagnosis = new EncounterTransaction.Diagnosis(){{
            this.setExistingObs("Diagnosis-Uuid");
        }};

        when(conceptService.getConceptByName(BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS)).thenReturn(new Concept());
        when(conceptService.getConceptByName(BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_STATUS)).thenReturn(new Concept());
        when(conceptService.getConceptByName(BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_REVISED)).thenReturn(new Concept() {{
            this.setDatatype(new ConceptDatatype() {{
                setUuid(BOOLEAN_UUID);
            }});
        }});

        when(conceptService.getTrueConcept()).thenReturn(new Concept());
        when(conceptService.getFalseConcept()).thenReturn(new Concept());


        BahmniDiagnosisHelper diagnosisHelper = new BahmniDiagnosisHelper(obsService, conceptService);

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);

        diagnosisHelper.updateDiagnosisMetaData(bahmniDiagnosis, diagnosis, encounter);

        assertEquals(encounter.getAllObs().iterator().next().getComment(), comments);
    }
}
