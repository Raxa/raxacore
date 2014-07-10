package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.util.LocaleUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class BahmniObservationsMappperTest {
    private BahmniObservationsMapper bahmniObservationsMappper;

    @Before
    public void setUp() throws Exception {
        bahmniObservationsMappper = new BahmniObservationsMapper();
    }

    private Obs obsBuilder(String patientUuid, Date visitStartDateTime, String visitUuid, String conceptName,
                           String encounterUuid, Date encounterDateTime, String conceptDataType,
                           String obsValue, Date obsDateTime, Set<Obs> groupMembers, String conceptUuid) throws ParseException {
        Concept concept = conceptBuilder(conceptName, conceptDataType, conceptUuid);
        Person person = personBuilder(patientUuid);
        Visit visit = visitBuilder(person, visitUuid, visitStartDateTime);
        Encounter encounter = encounterBuilder(visit, person, encounterUuid, encounterDateTime);
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setEncounter(encounter);
        obs.setPerson(person);
        obs.setObsDatetime(obsDateTime);
        obs.setValueText(obsValue);
        obs.setGroupMembers(groupMembers);
        return obs;
    }

    private Encounter encounterBuilder(Visit visit, Person person, String encounterUuid, Date encounterDateTime) {
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(encounterDateTime);
        encounter.setPatient(new Patient(person));
        encounter.setVisit(visit);
        encounter.setUuid(encounterUuid);
        return encounter;
    }

    private Visit visitBuilder(Person person, String visitUuid, Date visitStartDateTime) {
        Visit visit = new Visit();
        visit.setPatient(new Patient(person));
        visit.setStartDatetime(visitStartDateTime);
        visit.setUuid(visitUuid);
        return visit;
    }


    private Person personBuilder(String patientUuid) {
        Person person = new Person();
        person.setUuid(patientUuid);
        return person;
    }

    private Concept conceptBuilder(String conceptName, String conceptDataType, String uuid) {
        List<ConceptName> conceptNames = new ArrayList<>();
        conceptNames.add(new ConceptName(conceptName, LocaleUtility.getDefaultLocale()));
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setHl7Abbreviation(conceptDataType);
        conceptDatatype.setName(conceptDataType);
        Concept concept = new Concept();
        concept.setDatatype(conceptDatatype);
        concept.setNames(conceptNames);
        concept.setUuid(uuid);
        return concept;
    }

    @Test
    public void return_empty_list_for_no_obs() throws Exception {
        assertEquals(0, bahmniObservationsMappper.map(new ArrayList<Obs>()).size());
    }

    @Test
    public void return_mapped_observation_for_observation_without_groupmembers() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Obs obs = obsBuilder("puuid", date, "vuuid", "tconcept", "euuid", date, "cdatatype", "ovalue", date, null, "cuuid");
        ArrayList<Obs> observations = new ArrayList<Obs>();
        observations.add(obs);
        List<ObservationData> mappedObservations = bahmniObservationsMappper.map(observations);
        assertEquals(1, mappedObservations.size());
        ObservationData observationData = mappedObservations.get(0);
        assertEquals(obs.getConcept().getName(LocaleUtility.getDefaultLocale()).getName(), observationData.getConcept().getName());
        assertEquals(obs.getEncounter().getVisit().getUuid(), observationData.getVisit().getUuid());
        assertEquals(obs.getEncounter().getVisit().getStartDatetime(), observationData.getVisit().getStartDateTime());
        assertEquals(obs.getPerson().getUuid(), observationData.getPatient().getUuid());
        assertEquals(obs.getConcept().getDatatype().getName(), observationData.getValue().getConceptDataType());
    }
}
