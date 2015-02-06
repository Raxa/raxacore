package org.bahmni.module.bahmnicoreui.mapper;


import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;

import java.util.*;

public class DiseaseSummaryObsMapper extends DiseaseSummaryMapper<Collection<BahmniObservation>> {

    public Map<String, Map<String, ConceptValue>> map(Collection<BahmniObservation> bahmniObservations, String groupBy) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        List<BahmniObservation> finalObservations = new ArrayList<>();
        for (BahmniObservation myObservation : bahmniObservations) {
            constructLeafObservationsFromConceptSet(myObservation, finalObservations);
        }
        if (bahmniObservations != null) {
            Map<String, List<BahmniObservation>> observationsByEncounter = groupObsByEncounterUuid(finalObservations);
            for (BahmniObservation bahmniObservation : bahmniObservations) {
                List<BahmniObservation> observationsFromConceptSet = new ArrayList<>();
                constructLeafObservationsFromConceptSet(bahmniObservation, observationsFromConceptSet);
                for (BahmniObservation leafObservation : observationsFromConceptSet) {
                    String startDateTime = getGroupByDate(leafObservation, groupBy);
                    String conceptName = getConceptNameToDisplay(leafObservation);
                    String observationValue = computeValueForLeafObservation(leafObservation, observationsByEncounter);
                    addToResultTable(result, startDateTime, conceptName, observationValue, leafObservation.isAbnormal(), false);
                }
            }
        }
        return result;
    }

    private Map<String, List<BahmniObservation>> groupObsByEncounterUuid(Collection<BahmniObservation> bahmniObservations) {
        Map<String,List<BahmniObservation>> result = new LinkedHashMap<>();
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            List<BahmniObservation> bahmniObservationsForEncounter = result.get(bahmniObservation.getEncounterUuid()) == null?new ArrayList<BahmniObservation>():result.get(bahmniObservation.getEncounterUuid());
            bahmniObservationsForEncounter.add(bahmniObservation);
            result.put(bahmniObservation.getEncounterUuid(), bahmniObservationsForEncounter);
        }
        return result;
    }

    private void constructLeafObservationsFromConceptSet(BahmniObservation bahmniObservation, List<BahmniObservation> observationsFromConceptSet) {
        if (bahmniObservation.getGroupMembers().size() > 0) {
            for (BahmniObservation groupMember : bahmniObservation.getGroupMembers())
                constructLeafObservationsFromConceptSet(groupMember, observationsFromConceptSet);
        } else {
            if (!ETObsToBahmniObsMapper.ABNORMAL_CONCEPT_CLASS.equals(bahmniObservation.getConcept().getConceptClass())) {
                observationsFromConceptSet.add(bahmniObservation);
            }
        }
    }

    private String computeValueForLeafObservation(BahmniObservation observation, Map<String, List<BahmniObservation>> observationsByEncounter) {
        String observationValue = null;
        if (observationsByEncounter.containsKey(observation.getEncounterUuid())) {
            List<BahmniObservation> observationsInEncounter = observationsByEncounter.get(observation.getEncounterUuid());
            String multiSelectObsValue = "";
            for (BahmniObservation bahmniObservationInEncounter : observationsInEncounter) {
                if (arePartOfMultiSelectObservation(observation,bahmniObservationInEncounter)) {
                    multiSelectObsValue = multiSelectObsValue + "," + getObsValueAsString(bahmniObservationInEncounter.getValue());
                }
            }
            observationValue = StringUtils.isBlank(multiSelectObsValue)?getObsValueAsString(observation.getValue()):getObsValueAsString(observation.getValue())+multiSelectObsValue;
        }
        return observationValue;
    }

    private boolean arePartOfMultiSelectObservation(BahmniObservation observation,BahmniObservation observationInEncounter){
        return observation.getConcept().getName().equals(observationInEncounter.getConcept().getName())
                && StringUtils.equals(observation.getObsGroupUuid(),observationInEncounter.getObsGroupUuid())
                && !observation.getUuid().equals(observationInEncounter.getUuid());
    }

}
