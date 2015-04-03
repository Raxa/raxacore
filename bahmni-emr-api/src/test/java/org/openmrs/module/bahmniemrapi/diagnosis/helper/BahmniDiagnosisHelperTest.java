package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bahmni.test.builder.DiagnosisBuilder;
import org.bahmni.test.builder.ObsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.hamcrest.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({Context.class,LocaleUtility.class})
@RunWith(PowerMockRunner.class)
public class BahmniDiagnosisHelperTest {
    @Mock
    private ObsService obsService;
    @Mock
    private ConceptService conceptService;

    @Mock(answer= Answers.RETURNS_DEEP_STUBS)
    private EmrApiProperties properties;

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


        BahmniDiagnosisHelper diagnosisHelper = new BahmniDiagnosisHelper(obsService, conceptService,properties);

        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);

        diagnosisHelper.updateDiagnosisMetaData(bahmniDiagnosis, diagnosis, encounter);

        assertEquals(encounter.getAllObs().iterator().next().getComment(), comments);
    }

    @Test
    public void shouldGetLatestDiagnosisBasedOnCurrentDiagnosis(){
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<>(Arrays.asList(Locale.getDefault())));
        when(Context.getConceptService()).thenReturn(conceptService);

        Obs diagnosisObs = new DiagnosisBuilder()
                .withDefaults()
                .withFirstObs("firstDiagnosisObsId")
                .withUuid("firstDiagnosisObsId")
                .build();

        Obs updatedDiagnosisObs = new DiagnosisBuilder()
                .withDefaults()
                .withFirstObs("firstDiagnosisObsId")
                .withUuid("finalDiagnosisUuid")
                .build();

        Obs bahmniDiagnosisRevised = new ObsBuilder().withConcept(BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_REVISED,Locale.getDefault()).withValue("false").build();
        bahmniDiagnosisRevised.setObsGroup(updatedDiagnosisObs);

        when(obsService.getObservations(anyListOf(Person.class), anyList(),anyListOf(Concept.class),anyListOf(Concept.class), anyList(), anyList(), anyList(),
                anyInt(), anyInt(),  Matchers.any(java.util.Date.class), Matchers.any(java.util.Date.class), eq(false)))
                .thenReturn(Arrays.asList(bahmniDiagnosisRevised));

        Diagnosis mockedDiagnosis = mock(Diagnosis.class,RETURNS_DEEP_STUBS);
        Concept mockedConcept = mock(Concept.class);
        DiagnosisMetadata diagnosisMetadata = mock(DiagnosisMetadata.class);

        when(properties.getDiagnosisMetadata().toDiagnosis(updatedDiagnosisObs)).thenReturn(mockedDiagnosis);
        when(properties.getSuppressedDiagnosisConcepts()).thenReturn(new ArrayList<Concept>());
        when(properties.getNonDiagnosisConceptSets()).thenReturn(new ArrayList<Concept>());
        when(mockedDiagnosis.getDiagnosis().getCodedAnswer()).thenReturn(mockedConcept);

        BahmniDiagnosisHelper diagnosisHelper = new BahmniDiagnosisHelper(obsService, conceptService,properties);
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setExistingObs(diagnosisObs);

        Diagnosis actualDiagnosis = diagnosisHelper.getLatestBasedOnAnyDiagnosis(diagnosis);

        Assert.assertEquals(mockedDiagnosis,actualDiagnosis);
    }

/*    @Test
    public void shouldGetReturnNullWhenNoLatestDiagnosisIsAvailable(){
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<>(Arrays.asList(Locale.getDefault())));
        when(Context.getConceptService()).thenReturn(conceptService);

        Obs diagnosisObs = new DiagnosisBuilder()
                .withDefaults()
                .withFirstObs("firstDiagnosisObsId")
                .withUuid("firstDiagnosisObsId")
                .build();

        Obs updatedDiagnosisObs = new DiagnosisBuilder()
                .withDefaults()
                .withFirstObs("someOtherDiagnosisObsId")
                .withUuid("finalDiagnosisUuid")
                .build();

        Obs bahmniDiagnosisRevised = new ObsBuilder().withConcept(BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_REVISED,Locale.getDefault()).withValue("false").build();
        bahmniDiagnosisRevised.setObsGroup(updatedDiagnosisObs);

        when(obsService.getObservations(anyListOf(Person.class), anyList(),anyListOf(Concept.class),anyListOf(Concept.class), anyList(), anyList(), anyList(),
                anyInt(), anyInt(),  Matchers.any(java.util.Date.class), Matchers.any(java.util.Date.class), eq(false)))
                .thenReturn(Arrays.asList(bahmniDiagnosisRevised));


        BahmniDiagnosisHelper diagnosisHelper = new BahmniDiagnosisHelper(obsService, conceptService);
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setExistingObs(diagnosisObs);

        Obs actualUpdatedDiagnosisObs = diagnosisHelper.getLatestBasedOnAnyDiagnosis(diagnosis);

        Assert.assertNull(actualUpdatedDiagnosisObs);
    }*/

}
