package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.bahmni.module.bahmnicore.mapper.builder.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.util.LocaleUtility;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BahmniObservationsMapperTest {
    private BahmniObservationsMapper bahmniObservationsMapper;

    @Before
    public void setUp() throws Exception {
        bahmniObservationsMapper = new BahmniObservationsMapper();
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
        Concept concept1 = new ConceptBuilder().withName("tconcept1").withDataType("cdatatype").withUUID("cuuid1").withClass("").build();

        Obs obs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept1).withValue("ovalue1").withDatetime(date).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(obs));

        assertEquals(1, mappedObservations.size());
        ObservationData observationData = mappedObservations.get(0);
        assertEquals(obs.getConcept().getName(LocaleUtility.getDefaultLocale()).getName(), observationData.getConcept().getName());
        assertEquals(obs.getEncounter().getVisit().getUuid(), observationData.getVisit().getUuid());
        assertEquals(obs.getEncounter().getVisit().getStartDatetime(), observationData.getVisit().getStartDateTime());
        assertEquals(obs.getPerson().getUuid(), observationData.getPatient().getUuid());
        assertEquals(obs.getConcept().getDatatype().getName(), observationData.getValueData().getConceptDataType());
    }

    @Test
    public void return_mapped_observations_for_only_leaf_values() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();
        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype").withUUID("cuuid").withClass("").build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withDataType("cdatatype").withUUID("cuuid1").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withDataType("cdatatype").withUUID("cuuid2").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue("ovalue1").withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue("ovalue2").withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        assertEquals(2, mappedObservations.size());
        ObservationData observationData1 = mappedObservations.get(0);
        ObservationData observationData2 = mappedObservations.get(1);
        assertEquals("puuid", observationData1.getPatient().getUuid());
        assertEquals("puuid", observationData2.getPatient().getUuid());
        assertEquals("vuuid", observationData2.getVisit().getUuid());
        assertEquals("vuuid", observationData2.getVisit().getUuid());
        assertEquals(0, observationData1.getDuration());
        assertEquals(0, observationData2.getDuration());
        assertEquals(false, observationData1.isAbnormal());
        assertEquals(false, observationData2.isAbnormal());
        String[] concepts = {"tconcept1", "tconcept2"};
        String[] obsValues = {"ovalue1", "ovalue2"};
        assertTrue(Arrays.asList(concepts).contains(observationData1.getConcept().getName()));
        assertTrue(Arrays.asList(concepts).contains(observationData2.getConcept().getName()));
        assertTrue(Arrays.asList(obsValues).contains(observationData1.getValueData().getValue()));
        assertTrue(Arrays.asList(obsValues).contains(observationData2.getValueData().getValue()));
    }

    @Test
    public void return_mapped_observations_for_abnormal_observation_structure() throws Exception {

        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();

        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype").withUUID("cuuid").withClass(BahmniObservationsMapper.CONCEPT_DETAILS_CONCEPT_CLASS).build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withDataType("CODED").withUUID("cuuid1").withClass(BahmniObservationsMapper.ABNORMAL_CONCEPT_CLASS).build();
        Concept concept111 = new ConceptBuilder().withName("True").withDataType("cdatatype").withUUID("cuuid11").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withDataType("cdatatype").withUUID("cuuid2").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue(concept111).withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue("ovalue").withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        ObservationData observationData = mappedObservations.get(0);
        assertEquals(1, mappedObservations.size());
        assertTrue(observationData.isAbnormal());
        assertEquals("ovalue", observationData.getValueData().getValue());
        assertEquals("cdatatype", observationData.getValueData().getConceptDataType());
    }

    @Test
    public void return_mapped_observations_for_abnormal_and_coded_observation_structure() throws Exception {

        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPerson(person).withUUID("euuid").withDatetime(date).build();

        Concept concept1 = new ConceptBuilder().withName("tconcept").withDataType("cdatatype").withUUID("cuuid").withClass(BahmniObservationsMapper.CONCEPT_DETAILS_CONCEPT_CLASS).build();
        Concept concept11 = new ConceptBuilder().withName("tconcept1").withDataType("CODED").withUUID("cuuid1").withClass(BahmniObservationsMapper.ABNORMAL_CONCEPT_CLASS).build();
        Concept concept111 = new ConceptBuilder().withName("True").withDataType("cdatatype").withUUID("cuuid11").withClass("").build();
        Concept concept12 = new ConceptBuilder().withName("tconcept2").withDataType(ConceptDatatype.CODED).withUUID("cuuid2").withClass("").build();
        Concept concept112 = new ConceptBuilder().withName("tconcept3").withDataType("answer").withUUID("cuuid12").withClass("").build();

        Obs obs11 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept11).withValue(concept111).withDatetime(date).build();
        Obs obs12 = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(concept12).withValue(concept112).withDatetime(date).build();
        Obs observations = new ObsBuilder().withConcept(concept1).withGroupMembers(obs11, obs12).build();

        List<ObservationData> mappedObservations = bahmniObservationsMapper.map(Arrays.asList(observations));

        ObservationData observationData = mappedObservations.get(0);
        assertEquals(1, mappedObservations.size());
        assertTrue(observationData.isAbnormal());
        assertEquals("tconcept3", observationData.getValueData().getValue());
        assertEquals("CWE", observationData.getValueData().getConceptDataType());
    }

}
