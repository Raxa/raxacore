package org.openmrs.module.bahmnicore.web.v1_0.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniObservationMapperTest {
    @Mock
    ConceptService conceptService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldSortObservationsFromEncounterTransactions() throws Exception {
        EncounterTransaction encounterTransaction = new EncounterTransaction();
        EncounterTransaction.Observation obsGroup = new EncounterTransaction.Observation();
        Concept obsGroupConcept = new Concept();
        Concept obs1Concept = new Concept();
        Concept obs2Concept = new Concept();
        obsGroupConcept.setSet(true);
        obsGroupConcept.addSetMember(obs2Concept);
        obsGroupConcept.addSetMember(obs1Concept);
        obsGroup.setConcept(new EncounterTransaction.Concept(obsGroupConcept.getUuid()));
        EncounterTransaction.Observation obs1 = new EncounterTransaction.Observation();
        obs1.setConcept(new EncounterTransaction.Concept(obs1Concept.getUuid()));
        EncounterTransaction.Observation obs2 = new EncounterTransaction.Observation();
        obs2.setConcept(new EncounterTransaction.Concept(obs2Concept.getUuid()));
        obsGroup.setGroupMembers(Arrays.asList(obs1, obs2));
        encounterTransaction.setObservations(Arrays.asList(obsGroup));


        when(conceptService.getConceptByUuid(obsGroupConcept.getUuid())).thenReturn(obsGroupConcept);
        BahmniObservationMapper bahmniObservationMapper = new BahmniObservationMapper(conceptService);
        List<EncounterTransaction.Observation> observations = bahmniObservationMapper.map(encounterTransaction);

        EncounterTransaction.Observation observationGroup = observations.get(0);
        Assert.assertEquals(obsGroupConcept.getUuid(), observationGroup.getConcept().getUuid());
        Assert.assertEquals(obs2Concept.getUuid(), observationGroup.getGroupMembers().get(0).getConcept().getUuid());
        Assert.assertEquals(obs1Concept.getUuid(), observationGroup.getGroupMembers().get(1).getConcept().getUuid());
    }

    @Test
    public void shouldSortObservationsRecursivelyFromEncounterTransactions() throws Exception {
        EncounterTransaction encounterTransaction = new EncounterTransaction();
        Concept obsGroupConcept = new Concept();
        Concept obsGroup2Concept = new Concept();
        Concept obs1Concept = new Concept();
        Concept obs2Concept = new Concept();
        Concept obs3Concept = new Concept();
        obsGroup2Concept.setSet(true);
        obsGroup2Concept.addSetMember(obs3Concept);
        obsGroup2Concept.addSetMember(obs2Concept);
        obsGroupConcept.setSet(true);
        obsGroupConcept.addSetMember(obs1Concept);
        obsGroupConcept.addSetMember(obsGroup2Concept);
        EncounterTransaction.Observation obsGroup = new EncounterTransaction.Observation();
        obsGroup.setConcept(new EncounterTransaction.Concept(obsGroupConcept.getUuid()));
        EncounterTransaction.Observation obsGroup2 = new EncounterTransaction.Observation();
        obsGroup2.setConcept(new EncounterTransaction.Concept(obsGroup2Concept.getUuid()));
        EncounterTransaction.Observation obs1 = new EncounterTransaction.Observation();
        obs1.setConcept(new EncounterTransaction.Concept(obs1Concept.getUuid()));
        EncounterTransaction.Observation obs2 = new EncounterTransaction.Observation();
        obs2.setConcept(new EncounterTransaction.Concept(obs2Concept.getUuid()));
        EncounterTransaction.Observation obs3 = new EncounterTransaction.Observation();
        obs3.setConcept(new EncounterTransaction.Concept(obs3Concept.getUuid()));
        obsGroup2.setGroupMembers(Arrays.asList(obs2, obs3));
        obsGroup.setGroupMembers(Arrays.asList(obsGroup2, obs1));
        encounterTransaction.setObservations(Arrays.asList(obsGroup));

        when(conceptService.getConceptByUuid(obsGroupConcept.getUuid())).thenReturn(obsGroupConcept);
        when(conceptService.getConceptByUuid(obsGroup2Concept.getUuid())).thenReturn(obsGroup2Concept);
        BahmniObservationMapper bahmniObservationMapper = new BahmniObservationMapper(conceptService);
        List<EncounterTransaction.Observation> observations = bahmniObservationMapper.map(encounterTransaction);

        EncounterTransaction.Observation observationGroup = observations.get(0);
        Assert.assertEquals(obsGroupConcept.getUuid(), observationGroup.getConcept().getUuid());
        Assert.assertEquals(obs1Concept.getUuid(), observationGroup.getGroupMembers().get(0).getConcept().getUuid());
        EncounterTransaction.Observation observationGroup2 = observationGroup.getGroupMembers().get(1);
        Assert.assertEquals(obsGroup2Concept.getUuid(), observationGroup2.getConcept().getUuid());
        Assert.assertEquals(obs3Concept.getUuid(), observationGroup2.getGroupMembers().get(0).getConcept().getUuid());
        Assert.assertEquals(obs2Concept.getUuid(), observationGroup2.getGroupMembers().get(1).getConcept().getUuid());
    }
}
