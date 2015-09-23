package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.builder.PersonBuilder;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)

public class ETObsToBahmniObsMapperTest {

    @Mock
    ConceptService conceptService;

    ETObsToBahmniObsMapper etObsToBahmniObsMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        etObsToBahmniObsMapper = new ETObsToBahmniObsMapper(conceptService);
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);

    }

    @Test
    public void testMap() throws Exception {
        String person1name = "superman";
        String person2name = "RajSingh";
        String encounterUuid = "encounter-uuid";
        String obsGroupUuid = "obs-group-uuid";

        Person person1 = new PersonBuilder().withUUID("puuid1").withPersonName(person1name).build();
        User user1 = new User(person1);
        Person person2 = new PersonBuilder().withUUID("puuid2").withPersonName(person2name).build();
        User user2 = new User(person2);

        EncounterTransaction.Concept etParentConcept = new EncounterTransaction.Concept();
        etParentConcept.setDataType("N/A");
        etParentConcept.setConceptClass("Misc");
        EncounterTransaction.Concept etValueConcept = new EncounterTransaction.Concept();
        etValueConcept.setDataType("text");
        etValueConcept.setConceptClass("Misc");

        Concept valueConcept = new org.openmrs.module.bahmniemrapi.builder.ConceptBuilder().withName("valueConcept").withDataType("text").withUUID("cuuid2").withClass("").build();
        Concept parentConcept = new org.openmrs.module.bahmniemrapi.builder.ConceptBuilder().withName("parentConcept").withDataType("N/A").build();
        parentConcept.addSetMember(valueConcept);

        EncounterTransaction.Observation observation1 = new EncounterTransaction.Observation();
        observation1.setUuid("obs1-uuid");
        observation1.setCreator(user1);
        observation1.setValue("notes");
        observation1.setConcept(etValueConcept);
        EncounterTransaction.Observation observation2 = new EncounterTransaction.Observation();
        observation2.setUuid("obs2-uuid");
        observation2.setCreator(user2);
        observation2.setConcept(etParentConcept);
        observation2.setGroupMembers(asList(observation1));

        AdditionalBahmniObservationFields additionalBahmniObservationFields = new AdditionalBahmniObservationFields(encounterUuid, new Date(), new Date(), obsGroupUuid);

        BahmniObservation actualObs = etObsToBahmniObsMapper.map(observation2, additionalBahmniObservationFields, asList(parentConcept), false);

        assertEquals(person2name, actualObs.getCreatorName());
        assertEquals(encounterUuid, actualObs.getEncounterUuid());
        assertEquals(obsGroupUuid, actualObs.getObsGroupUuid());
        BahmniObservation actualValueObs = actualObs.getGroupMembers().iterator().next();
        assertEquals(person1name, actualValueObs.getCreatorName());
        assertEquals("obs2-uuid", actualValueObs.getObsGroupUuid());
    }
}