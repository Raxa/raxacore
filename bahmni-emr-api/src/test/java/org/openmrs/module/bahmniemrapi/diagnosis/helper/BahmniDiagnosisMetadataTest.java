package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.openmrs.module.bahmniemrapi.builder.ObsBuilder;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
        mockStatic(LocaleUtility.class);
        mockStatic(Context.class);
        PowerMockito.when(Context.getLocale()).thenReturn(Locale.ENGLISH);
        PowerMockito.when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);
    }


    @Test
    public void shouldMatchCodedDiagnosis() {
        Concept diagnosisSetConcept = conceptForName("Diagnosis Concept Set");
        Concept codedDiagnosisConcept = conceptForName("Coded Diagnosis");
        when(properties.getDiagnosisMetadata().getDiagnosisSetConcept()).thenReturn(diagnosisSetConcept);
        when(properties.getDiagnosisMetadata().getCodedDiagnosisConcept()).thenReturn(codedDiagnosisConcept);
        Concept feverConcept = conceptForName("Fever");
        Obs diagnosisObs =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                 new ObsBuilder().withConcept(codedDiagnosisConcept)
                                        .withValue(feverConcept).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();
        Obs anotherDiagnosis =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                new ObsBuilder().withConcept(codedDiagnosisConcept)
                                        .withValue(conceptForName("Another diagnosis")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();

        Obs randomObs = new ObsBuilder().withConcept(conceptForName("Random Concept")).withValue("Hello World").build();

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setCodedAnswer(new EncounterTransaction.Concept(feverConcept.getUuid(), feverConcept.getName
                ().getName(), false));

        BahmniDiagnosisMetadata bahmniDiagnosisMetadata = new BahmniDiagnosisMetadata(obsService, conceptService, properties, null);
        Obs matchingDiagnosis = bahmniDiagnosisMetadata.findMatchingDiagnosis(Arrays.asList(diagnosisObs,
                anotherDiagnosis,
                randomObs), bahmniDiagnosis);
        assertThat(matchingDiagnosis, is(sameInstance(diagnosisObs)));

    }

    @Test
    public void shouldMatchNonCodedDiagnosis() {
        Concept diagnosisSetConcept = conceptForName("Diagnosis Concept Set");
        Concept codedDiagnosisConcept = conceptForName("Coded Diagnosis");
        Concept nonCodedDiagnosisConcept = conceptForName("Non Coded Diagnosis");
        when(properties.getDiagnosisMetadata().getDiagnosisSetConcept()).thenReturn(diagnosisSetConcept);
        when(properties.getDiagnosisMetadata().getCodedDiagnosisConcept()).thenReturn(codedDiagnosisConcept);
        when(properties.getDiagnosisMetadata().getNonCodedDiagnosisConcept()).thenReturn(nonCodedDiagnosisConcept);
        Obs codedDiagnosisObs =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                 new ObsBuilder().withConcept(codedDiagnosisConcept)
                                        .withValue(conceptForName("Fever")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();
        Obs nonCodedDiagnosisObs =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                new ObsBuilder().withConcept(nonCodedDiagnosisConcept)
                                        .withValue("Free text diagnosis").build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();

        Obs randomObs = new ObsBuilder().withConcept(conceptForName("Random Concept")).withValue("Hello World").build();

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setFreeTextAnswer("Free text diagnosis");

        BahmniDiagnosisMetadata bahmniDiagnosisMetadata = new BahmniDiagnosisMetadata(obsService, conceptService, properties, null);
        Obs matchingDiagnosis = bahmniDiagnosisMetadata.findMatchingDiagnosis(Arrays.asList(codedDiagnosisObs,
                nonCodedDiagnosisObs,
                randomObs), bahmniDiagnosis);
        assertThat(matchingDiagnosis, is(sameInstance(nonCodedDiagnosisObs)));

    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenMoreThanOneMatchingDiagnosisFound() {
        Concept diagnosisSetConcept = conceptForName("Diagnosis Concept Set");
        Concept codedDiagnosisConcept = conceptForName("Coded Diagnosis");
        Concept nonCodedDiagnosisConcept = conceptForName("Non Coded Diagnosis");
        when(properties.getDiagnosisMetadata().getDiagnosisSetConcept()).thenReturn(diagnosisSetConcept);
        when(properties.getDiagnosisMetadata().getCodedDiagnosisConcept()).thenReturn(codedDiagnosisConcept);
        when(properties.getDiagnosisMetadata().getNonCodedDiagnosisConcept()).thenReturn(nonCodedDiagnosisConcept);
        Concept feverConcept = conceptForName("Fever");
        Obs aCodedDiagnosisObs =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                 new ObsBuilder().withConcept(codedDiagnosisConcept)
                                        .withValue(feverConcept).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();
        Obs anotherCodedDiagnosisObs =
                new ObsBuilder().withConcept(diagnosisSetConcept)
                        .withGroupMembers(
                                 new ObsBuilder().withConcept(codedDiagnosisConcept)
                                        .withValue(feverConcept).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Order"))
                                        .withValue(conceptForName("Primary")).build(),
                                new ObsBuilder().withConcept(conceptForName("Diagnosis Certainty"))
                                        .withValue(conceptForName("Confirmed")).build()
                        ).build();
        Obs randomObs = new ObsBuilder().withConcept(conceptForName("Random Concept")).withValue("Hello World").build();

        BahmniDiagnosis bahmniDiagnosis = new BahmniDiagnosis();
        bahmniDiagnosis.setCodedAnswer(new EncounterTransaction.Concept(feverConcept.getUuid(), feverConcept.getName
                ().getName(), false));

        BahmniDiagnosisMetadata bahmniDiagnosisMetadata = new BahmniDiagnosisMetadata(obsService, conceptService, properties, null);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("The same diagnosis cannot be saved more than once");
        bahmniDiagnosisMetadata.findMatchingDiagnosis(
                Arrays.asList(aCodedDiagnosisObs, anotherCodedDiagnosisObs, randomObs), bahmniDiagnosis);
    }

    public Concept conceptForName(String conceptName) {
        return new ConceptBuilder().withName(conceptName).build();
    }

}
