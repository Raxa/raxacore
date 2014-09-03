package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.bahmni.module.obsrelationship.api.ObsRelationService;
import org.bahmni.module.obsrelationship.model.ObsRelationship;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObsRelationshipMapper {

    @Autowired
    private ObsRelationService obsRelationService;

    @Autowired
    private ObservationMapper observationMapper;

    public ObsRelationshipMapper(ObsRelationService obsRelationService, ObservationMapper observationMapper) {

        this.obsRelationService = obsRelationService;
        this.observationMapper = observationMapper;
    }

    public List<BahmniObservation> map(List<BahmniObservation> bahmniObservations,String encounterUuid){
        List<ObsRelationship> obsRelationshipsInEncounter = obsRelationService.getRelationsWhereSourceObsInEncounter(encounterUuid);
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            for (ObsRelationship obsRelationship : obsRelationshipsInEncounter) {
                if(bahmniObservation.isSameAs(obsRelationship.getSourceObs())){
                    org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship targetObsRelation = new org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship();
                    targetObsRelation.setRelationshipType(obsRelationship.getObsRelationshipType().getName());
                    targetObsRelation.setUuid(obsRelationship.getUuid());
                    EncounterTransaction.Observation etObservation = observationMapper.map(obsRelationship.getTargetObs());
                    targetObsRelation.setTargetObs(new BahmniObservation(etObservation));
                    bahmniObservation.setTargetObsRelation(targetObsRelation);
                }
            }
        }
        return bahmniObservations;
    }
}
