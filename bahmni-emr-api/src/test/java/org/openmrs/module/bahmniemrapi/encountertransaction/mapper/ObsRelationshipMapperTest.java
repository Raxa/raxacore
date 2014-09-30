package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.bahmni.module.obsrelationship.api.ObsRelationService;
import org.bahmni.module.obsrelationship.model.ObsRelationship;
import org.bahmni.module.obsrelationship.model.ObsRelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.*;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction.Observation;

import java.util.*;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(LocaleUtility.class)
@RunWith(PowerMockRunner.class)
public class ObsRelationshipMapperTest {

    @Mock
    private ObsRelationService obsrelationService;
    @Mock
    private ObservationMapper observationMapper;
    @Mock
    private EncounterProviderMapper encounterProviderMapper;

    private ObsRelationshipMapper obsRelationshipMapper;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(LocaleUtility.class);
        PowerMockito.when(LocaleUtility.getLocalesInOrder()).thenReturn(new HashSet<Locale>(Arrays.asList(Locale.getDefault())));

        initMocks(this);
        obsRelationshipMapper = new ObsRelationshipMapper(obsrelationService, observationMapper, encounterProviderMapper);
    }

    @Test
    public void shouldMapObsRelationshipForBahmniObservations() {
        String sourceObsUuid = "source-obs-uuid";
        String targetObsUuid = "target-obs-uuid";

        Obs sourceObs = createObs(sourceObsUuid);
        Obs targetObs = createObs(targetObsUuid);

        List<ObsRelationship> obsRelationShips = new ArrayList<>();
        obsRelationShips.add(createObsRelationship(sourceObs, targetObs));

        EncounterTransaction.Observation mappedTargetObs = mapObs(targetObs);

        when(obsrelationService.getRelationsWhereSourceObsInEncounter("encounter-uuid")).thenReturn(obsRelationShips);
        when(observationMapper.map(targetObs)).thenReturn(mappedTargetObs);

        BahmniObservation sourceObservation = getBahmniObservation(sourceObsUuid);
        BahmniObservation targetObservation = getBahmniObservation(targetObsUuid);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        bahmniObservations.add(sourceObservation);
        bahmniObservations.add(targetObservation);

        HashSet<EncounterTransaction.Provider> providers = new HashSet<>();
        EncounterTransaction.Provider provider = new EncounterTransaction.Provider();
        provider.setName("superman");
        provider.setName("superUuid");
        providers.add(provider);

        List<BahmniObservation> mappedBahmniObservations = obsRelationshipMapper.map(bahmniObservations, "encounter-uuid", providers);

        verify(obsrelationService).getRelationsWhereSourceObsInEncounter("encounter-uuid");
        verify(observationMapper, times(1)).map(targetObs);
        assertEquals(2, mappedBahmniObservations.size());
        assertEquals(sourceObsUuid, mappedBahmniObservations.get(0).getUuid());
        assertEquals(targetObsUuid, mappedBahmniObservations.get(0).getTargetObsRelation().getTargetObs().getUuid());
        assertEquals("obsRelationType", mappedBahmniObservations.get(0).getTargetObsRelation().getRelationshipType());
        assertEquals(provider.getName(), mappedBahmniObservations.get(0).getProviders().iterator().next().getName());
        assertEquals(provider.getUuid(), mappedBahmniObservations.get(0).getProviders().iterator().next().getUuid());
    }

    @Test
    public void shouldMapMultipleObsRelationshipForBahmniObservations() {
        String sourceObs1Uuid = "source1-obs-uuid";
        String targetObs1Uuid = "target1-obs-uuid";

        String sourceObs2Uuid = "source2-obs-uuid";
        String targetObs2Uuid = "target2-obs-uuid";

        Obs sourceObs1 = createObs(sourceObs1Uuid);
        Obs sourceObs2 = createObs(sourceObs2Uuid);
        Obs targetObs1 = createObs(targetObs1Uuid);
        Obs targetObs2 = createObs(targetObs2Uuid);

        List<ObsRelationship> obsRelationShips = new ArrayList<>();
        obsRelationShips.add(createObsRelationship(sourceObs1, targetObs1));
        obsRelationShips.add(createObsRelationship(sourceObs2, targetObs2));

        EncounterTransaction.Observation mappedTargetObs1 = mapObs(targetObs1);
        EncounterTransaction.Observation mappedTargetObs2 = mapObs(targetObs2);

        when(obsrelationService.getRelationsWhereSourceObsInEncounter("encounter-uuid")).thenReturn(obsRelationShips);
        when(observationMapper.map(targetObs1)).thenReturn(mappedTargetObs1);
        when(observationMapper.map(targetObs2)).thenReturn(mappedTargetObs2);

        BahmniObservation sourceObservation1 = getBahmniObservation(sourceObs1Uuid);
        BahmniObservation sourceObservation2 = getBahmniObservation(sourceObs2Uuid);
        BahmniObservation targetObservation1 = getBahmniObservation(targetObs1Uuid);
        BahmniObservation targetObservation2 = getBahmniObservation(targetObs2Uuid);

        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        bahmniObservations.add(sourceObservation1);
        bahmniObservations.add(sourceObservation2);
        bahmniObservations.add(targetObservation1);
        bahmniObservations.add(targetObservation2);

        HashSet<EncounterTransaction.Provider> providers = new HashSet<>();
        EncounterTransaction.Provider provider = new EncounterTransaction.Provider();
        provider.setName("superman");
        provider.setName("superUuid");
        providers.add(provider);

        List<BahmniObservation> mappedBahmniObservations = obsRelationshipMapper.map(bahmniObservations, "encounter-uuid", providers);

        verify(obsrelationService).getRelationsWhereSourceObsInEncounter("encounter-uuid");
        verify(observationMapper, times(2)).map(any(Obs.class));
        assertEquals(4, mappedBahmniObservations.size());
        assertEquals(sourceObs1Uuid, mappedBahmniObservations.get(0).getUuid());
        assertEquals(targetObs1Uuid, mappedBahmniObservations.get(0).getTargetObsRelation().getTargetObs().getUuid());
        assertEquals(sourceObs2Uuid, mappedBahmniObservations.get(1).getUuid());
        assertEquals(targetObs2Uuid, mappedBahmniObservations.get(1).getTargetObsRelation().getTargetObs().getUuid());
        assertEquals("obsRelationType", mappedBahmniObservations.get(0).getTargetObsRelation().getRelationshipType());
        assertEquals("obsRelationType", mappedBahmniObservations.get(1).getTargetObsRelation().getRelationshipType());
        assertEquals(provider.getName(), mappedBahmniObservations.get(0).getProviders().iterator().next().getName());
        assertEquals(provider.getUuid(), mappedBahmniObservations.get(0).getProviders().iterator().next().getUuid());
        assertEquals(provider.getName(), mappedBahmniObservations.get(1).getProviders().iterator().next().getName());
        assertEquals(provider.getUuid(), mappedBahmniObservations.get(1).getProviders().iterator().next().getUuid());
    }

    @Test
    public void shouldMapObsRelationshipsToBahmniObservations(){
        List<ObsRelationship> obsRelationships = new ArrayList<>();
        Obs sourceObs = createObs("sourceObsUuid");
        addEncounterProviders(sourceObs);

        Obs targetObs = createObs("targetObsUuid");

        ObsRelationship obsRelationship = createObsRelationship(sourceObs, targetObs);
        obsRelationships.add(obsRelationship);

        Observation mappedSourceObs = mapObs(sourceObs);
        Observation mappedTargetObs = mapObs(targetObs);
        Set<EncounterTransaction.Provider> providers = mapEncounterProviders(sourceObs.getEncounter().getEncounterProviders());

        when(observationMapper.map(sourceObs)).thenReturn(mappedSourceObs);
        when(observationMapper.map(targetObs)).thenReturn(mappedTargetObs);
        when(encounterProviderMapper.convert(sourceObs.getEncounter().getEncounterProviders())).thenReturn(providers);

        List<BahmniObservation> mappedObservations = obsRelationshipMapper.map(obsRelationships);

        BahmniObservation mappedObservation = mappedObservations.get(0);

        assertEquals("sourceObsUuid", mappedObservation.getUuid());
        assertNotNull("There are no providers.", mappedObservation.getProviders());
        assertEquals(sourceObs.getEncounter().getEncounterProviders().iterator().next().getUuid(), mappedObservation.getProviders().iterator().next().getUuid());
    }

    private Set<EncounterTransaction.Provider> mapEncounterProviders(Set<EncounterProvider> encounterProviders) {
        Set<EncounterTransaction.Provider> providers = new HashSet<>();
        for (EncounterProvider encounterProvider : encounterProviders) {
            EncounterTransaction.Provider provider = new EncounterTransaction.Provider();
            provider.setUuid(encounterProvider.getUuid());
            providers.add(provider);
        }
        return providers;
    }

    private void addEncounterProviders(Obs sourceObs) {
        EncounterProvider encounterProvider = new EncounterProvider();
        encounterProvider.setUuid("encounter-provider-uuid");

        HashSet<EncounterProvider> encounterProviders = new HashSet<>();
        encounterProviders.add(encounterProvider);

        Encounter encounter = new Encounter();
        encounter.setEncounterProviders(encounterProviders);

        sourceObs.setEncounter(encounter);
    }

    private BahmniObservation getBahmniObservation(String sourceObsUuid) {
        BahmniObservation sourceObservation = new BahmniObservation();
        sourceObservation.setUuid(sourceObsUuid);
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept("random-uuid", "Random Concept");
        sourceObservation.setConcept(concept);
        return sourceObservation;
    }

    private Observation mapObs(Obs targetObs) {
        EncounterTransaction.Observation mappedObs = new EncounterTransaction.Observation();
        mappedObs.setUuid(targetObs.getUuid());
        return mappedObs;
    }

    private Observation mapTargetObs(Obs targetObs) {
        EncounterTransaction.Observation mappedTargetObs = new EncounterTransaction.Observation();
        mappedTargetObs.setUuid(targetObs.getUuid());
        mappedTargetObs.setConcept(new EncounterTransaction.Concept(targetObs.getConcept().getUuid(), targetObs.getConcept().getName().getName()));
        return mappedTargetObs;
    }

    private ObsRelationship createObsRelationship(Obs sourceObs, Obs targetObs) {
        ObsRelationshipType obsRelationshipType = new ObsRelationshipType();
        obsRelationshipType.setName("obsRelationType");

        ObsRelationship obsRelationship = new ObsRelationship();
        obsRelationship.setObsRelationshipType(obsRelationshipType);
        obsRelationship.setSourceObs(sourceObs);
        obsRelationship.setTargetObs(targetObs);
        return obsRelationship;
    }

    private Obs createObs(String obsUuid) {
        Obs sourceObs = new Obs();
        sourceObs.setUuid(obsUuid);
        Concept concept = new Concept();
        concept.setFullySpecifiedName(new ConceptName("Random Concept", Locale.ENGLISH));
        sourceObs.setConcept(concept);
        return sourceObs;
    }
}
