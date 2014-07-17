package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.bahmni.module.bahmnicore.mapper.builder.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.*;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocaleUtility.class})
public class BahmniObservationsMapperTest {
    public static final String PATIENT_RESOURCE_URI = "/patient/Uri";
    public static final String ENCOUNTER_RESOURCE_URI = "/encounter/Uri";
    public static final String VISIT_RESOURCE_URI = "/visit/Uri";

    private BahmniObservationsMapper bahmniObservationsMapper;


    @Before
    public void setUp() throws Exception {
        Locale defaultLocale = new Locale("en", "GB");
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(defaultLocale);

        Resource mockResource = mock(Resource.class);
        when(mockResource.getUri(any())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments[0] instanceof Patient)
                    return PATIENT_RESOURCE_URI;
                else if (arguments[0] instanceof Encounter)
                    return ENCOUNTER_RESOURCE_URI;
                else if (arguments[0] instanceof Visit)
                    return VISIT_RESOURCE_URI;

                return null;
            }
        });
        RestService mockRestService = mock(RestService.class);
        when(mockRestService.getResourceByName(anyString())).thenReturn(mockResource);
        String[] conceptNames = {"tconcept1", "tconcept2", "True", "tconcept", "tconcept3"};
        bahmniObservationsMapper = new BahmniObservationsMapper(mockRestService, conceptNames);
    }

    @Test
    public void return_empty_list_for_no_obs() throws Exception {
        assertEquals(0, bahmniObservationsMapper.map(new ArrayList<Obs>()).size());
    }

    @Test
    public void return_mapped_observation_for_observation_without_groupmembers() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();
        Concept concept1 = new ConceptBuilder().withName("tconcept1").withDataTypeNumeric().withUUID("cuuid1").withClass("").build();

        Obs obs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept1).withValue(5.0).withDatetime(date).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(obs));

        assertEquals(1, mappedObservations.size());
        ObservationData observationData = mappedObservations.get(0);
        assertEquals(obs.getConcept().getName().getName(), observationData.getConcept());
        assertEquals(PATIENT_RESOURCE_URI, observationData.getLinks().getPatientURI());
        assertEquals(VISIT_RESOURCE_URI, observationData.getLinks().getVisitURI());
        assertEquals(ENCOUNTER_RESOURCE_URI, observationData.getLinks().getEncounterURI());
        assertEquals("5.0", observationData.getValue());
        assertEquals("Numeric", observationData.getType());
    }

    @Test
    public void return_mapped_observations_for_only_leaf_values() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();
        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid").withClass("").build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid1").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid2").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue("ovalue1").withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue("ovalue2").withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        assertEquals(2, mappedObservations.size());
        ObservationData observationData1 = mappedObservations.get(0);
        ObservationData observationData2 = mappedObservations.get(1);
        assertNull("Zero duration goes as null", observationData1.getDuration());
        assertNull("Zero duration goes as null", observationData2.getDuration());
        assertNull("isAbnormal should not be set", observationData1.getIsAbnormal());
        assertNull("isAbnormal should not be set", observationData2.getIsAbnormal());
        String[] concepts = {"tconcept1", "tconcept2"};
        String[] obsValues = {"ovalue1", "ovalue2"};
        assertTrue(Arrays.asList(concepts).contains(observationData1.getConcept()));
        assertTrue(Arrays.asList(concepts).contains(observationData2.getConcept()));
        assertTrue(Arrays.asList(obsValues).contains(observationData1.getValue()));
        assertTrue(Arrays.asList(obsValues).contains(observationData2.getValue()));
        System.out.println(observationData1.getRootConcept());
    }

    @Test
    public void return_mapped_observations_for_abnormal_observation_structure() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();

        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid").withClass(BahmniObservationsMapper.CONCEPT_DETAILS_CONCEPT_CLASS).build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withCodedDataType().withUUID("cuuid1").withClass(BahmniObservationsMapper.ABNORMAL_CONCEPT_CLASS).build();
        Concept concept111 = new ConceptBuilder().withName("True").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid11").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid2").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue(concept111).withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue("ovalue").withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        ObservationData observationData = mappedObservations.get(0);
        assertEquals(1, mappedObservations.size());
        assertTrue(observationData.getIsAbnormal());
        assertEquals("ovalue", observationData.getValue());
        assertEquals("cdatatype", observationData.getType());
    }

    @Test
    public void return_mapped_observations_for_abnormal_and_coded_observation_structure() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();

        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype").withUUID("cuuid").withClass(BahmniObservationsMapper.CONCEPT_DETAILS_CONCEPT_CLASS).build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withCodedDataType().withUUID("cuuid1").withClass(BahmniObservationsMapper.ABNORMAL_CONCEPT_CLASS).build();
        Concept concept111 = new ConceptBuilder().withName("True").withDataType("cdatatype").withUUID("cuuid11").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withCodedDataType().withUUID("cuuid2").withClass("").build();
        Concept concept112 = new ConceptBuilder().withName("tconcept3").withDataType("answer").withUUID("cuuid12").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue(concept111).withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue(concept112).withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        ObservationData observationData = mappedObservations.get(0);
        assertEquals(1, mappedObservations.size());
        assertTrue(observationData.getIsAbnormal());
        assertEquals("tconcept3", observationData.getValue());
    }

}
