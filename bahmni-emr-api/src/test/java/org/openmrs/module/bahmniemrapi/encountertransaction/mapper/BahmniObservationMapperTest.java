package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.builder.ConceptBuilder;
import org.openmrs.module.bahmniemrapi.builder.EncounterBuilder;
import org.openmrs.module.bahmniemrapi.builder.ObsBuilder;
import org.openmrs.module.bahmniemrapi.builder.PersonBuilder;
import org.openmrs.module.bahmniemrapi.builder.VisitBuilder;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)
public class BahmniObservationMapperTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void return_mapped_observations_for_abnormal_observation_structure() throws Exception {
        Date date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 2, 2010");
        Person person = new PersonBuilder().withUUID("puuid").build();
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(date).build();
        Encounter encounter = new EncounterBuilder().withVisit(visit).withPatient(person).withUUID("euuid").withDatetime(date).build();

        Concept parentConcept = new ConceptBuilder().withName("parentConcept").withDataType("N/A").build();
        Concept conceptDetailsConceptSet = new ConceptBuilder().withName("conceptDetailsConceptSet").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid").withClass(BahmniObservationMapper.CONCEPT_DETAILS_CONCEPT_CLASS).build();
        Concept abnormalConcept = new ConceptBuilder().withName("abnormalConcept").withCodedDataType().withUUID("cuuid1").withClass(BahmniObservationMapper.ABNORMAL_CONCEPT_CLASS).build();
        Concept durationConcept = new ConceptBuilder().withName("durationConcept").withDataTypeNumeric().withUUID("cuuid2").withClass(BahmniObservationMapper.DURATION_CONCEPT_CLASS).build();
        Concept trueConcept = new ConceptBuilder().withName("True").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid11").withClass("").build();
        Concept valueConcept = new ConceptBuilder().withName("valueConcept").withDataType("cdatatype", "hl7abbrev").withUUID("cuuid2").withClass("").build();

        Obs abnormalObs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(abnormalConcept).withValue(trueConcept).withDatetime(date).build();
        Obs durationObs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(durationConcept).withValue(10.0).withDatetime(date).build();
        Obs valueObs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(valueConcept).withValue("ovalue").withDatetime(date).build();
        Obs obs = new ObsBuilder().withConcept(conceptDetailsConceptSet).withGroupMembers(valueObs, abnormalObs, durationObs).build();
        Obs parentObs = new ObsBuilder().withPerson(person).withEncounter(encounter).withConcept(parentConcept).withDatetime(date).withGroupMembers(obs).build();
        
        List<BahmniObservation> parentsObservations = BahmniObservationMapper.map(asList(parentObs), Arrays.asList(parentConcept));
        assertEquals(1, parentsObservations.size());
        BahmniObservation parentObservation = parentsObservations.get(0);
        assertEquals("parentConcept", parentObservation.getConcept().getName());
        assertEquals(1, parentObservation.getGroupMembers().size());
        
        List<BahmniObservation> childObservations = parentObservation.getGroupMembers();
        assertEquals(1, childObservations.size());
        BahmniObservation childObservation = childObservations.get(0);
        assertEquals("ovalue", childObservation.getValue());
        assertEquals("cdatatype", childObservation.getType());
        assertTrue(childObservation.isAbnormal());
        assertEquals(10L, childObservation.getDuration().longValue());
    }

}
