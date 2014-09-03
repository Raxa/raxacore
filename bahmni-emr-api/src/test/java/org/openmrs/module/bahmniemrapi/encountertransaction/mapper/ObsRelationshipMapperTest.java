package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.bahmni.module.obsrelationship.api.ObsRelationService;
import org.bahmni.module.obsrelationship.api.impl.ObsRelationServiceImpl;
import org.bahmni.module.obsrelationship.model.ObsRelationship;
import org.bahmni.module.obsrelationship.model.ObsRelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObsRelationshipMapperTest {
    @Mock
    private ObsRelationService obsrelationService;
    @Mock
    private ObservationMapper observationMapper;

    private ObsRelationshipMapper obsRelationshipMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldMapObsRelationshipForBahmniObservations(){
        List<ObsRelationship> obsRelationShips = new ArrayList<>();
        Obs sourceObs = new Obs();
        sourceObs.setUuid("source-obs-uuid");

        Obs targetObs = new Obs();
        targetObs.setUuid("target-obs-uuid");

        ObsRelationship obsRelationship = new ObsRelationship();
        ObsRelationshipType obsRelationshipType = new ObsRelationshipType();
        obsRelationshipType.setName("obsRelationType");
        obsRelationship.setObsRelationshipType(obsRelationshipType);
        obsRelationship.setSourceObs(sourceObs);
        obsRelationship.setTargetObs(targetObs);

        obsRelationShips.add(obsRelationship);

        when(obsrelationService.getRelationsWhereSourceObsInEncounter("encounter-uuid")).thenReturn(obsRelationShips);

        EncounterTransaction.Observation mappedTargetObs = new EncounterTransaction.Observation();
        mappedTargetObs.setUuid(targetObs.getUuid());

        when(observationMapper.map(targetObs)).thenReturn(mappedTargetObs);

        BahmniObservation sourceObservation = new BahmniObservation();
        sourceObservation.setUuid("source-obs-uuid");

        BahmniObservation targetObservation = new BahmniObservation();
        targetObservation.setUuid("source-obs-uuid");
        ArrayList<BahmniObservation> bahmniObservations = new ArrayList<>();
        bahmniObservations.add(sourceObservation);
        bahmniObservations.add(targetObservation);

        obsRelationshipMapper = new ObsRelationshipMapper(obsrelationService, observationMapper);
        List<BahmniObservation> mappedBahmniObservations = obsRelationshipMapper.map(bahmniObservations, "encounter-uuid");

        assertEquals(2, mappedBahmniObservations.size());
        assertEquals("source-obs-uuid", mappedBahmniObservations.get(0).getUuid());
        assertEquals("target-obs-uuid", mappedBahmniObservations.get(0).getTargetObsRelation().getTargetObs().getUuid());
        assertEquals("obsRelationType", mappedBahmniObservations.get(0).getTargetObsRelation().getRelationshipType());
    }
}
