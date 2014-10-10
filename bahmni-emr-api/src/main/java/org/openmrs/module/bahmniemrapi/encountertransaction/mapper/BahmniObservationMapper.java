package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BahmniObservationMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    public static final String DURATION_CONCEPT_CLASS = "Duration";

    public static List<BahmniObservation> map(List<Obs> obsList) {
        return map(obsList, new ArrayList<Concept>());
    }

    // TODO : Shruthi : only this api should remain. The other map methods should go away. flatten option should be removed.
    public static List<BahmniObservation> map(List<Obs> obsList, List<Concept> rootConcepts) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (Obs obs : obsList) {
            bahmniObservations.add(map(new ObservationMapper().map(obs), obs.getEncounter().getEncounterDatetime(), rootConcepts, true));
        }
        return bahmniObservations;
    }

    public static BahmniObservation map(EncounterTransaction.Observation encounterTransactionObservation, Date encounterDateTime) {
        return map(encounterTransactionObservation, encounterDateTime, new ArrayList<Concept>());
    }

    public static BahmniObservation map(EncounterTransaction.Observation encounterTransactionObservation, Date encounterDateTime, List<Concept> rootConcepts) {
        return map(encounterTransactionObservation, encounterDateTime, rootConcepts, false);
    }

    private static BahmniObservation map(EncounterTransaction.Observation eTObservation, Date encounterDateTime, List<Concept> rootConcepts, boolean flatten) {
        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setEncounterTransactionObservation(eTObservation);
        bahmniObservation.setEncounterDateTime(encounterDateTime);
        bahmniObservation.setConceptSortWeight(ConceptSortWeightUtil.getSortWeightFor(bahmniObservation.getConcept().getName(), rootConcepts));
        if (CONCEPT_DETAILS_CONCEPT_CLASS.equals(eTObservation.getConcept().getConceptClass()) && flatten) {
            for (EncounterTransaction.Observation member : eTObservation.getGroupMembers()) {
                if (member.getVoided()) {
                    continue;
                }
                if (member.getConcept().getConceptClass().equals(ABNORMAL_CONCEPT_CLASS)) {
                    bahmniObservation.setAbnormal(Boolean.parseBoolean(((EncounterTransaction.Concept) member.getValue()).getName()));
                } else if (member.getConcept().getConceptClass().equals(DURATION_CONCEPT_CLASS)) {
                    bahmniObservation.setDuration(new Double(member.getValue().toString()).longValue());
                } else {
                    bahmniObservation.setValue(member.getValue());
                    bahmniObservation.setType(member.getConcept().getDataType());
                }
            }
        } else if (eTObservation.getGroupMembers().size() > 0) {
            for (EncounterTransaction.Observation groupMember : eTObservation.getGroupMembers()) {
                bahmniObservation.addGroupMember(map(groupMember, encounterDateTime, rootConcepts, flatten));
            }
        } else {
            bahmniObservation.setValue(eTObservation.getValue());
            bahmniObservation.setType(eTObservation.getConcept().getDataType());
        }
        return bahmniObservation;
    }


    public static List<BahmniObservation> toBahmniObsFromETObs(List<EncounterTransaction.Observation> allObservations, Date encounterDateTime) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : allObservations) {
            bahmniObservations.add(map(observation, encounterDateTime, new ArrayList<org.openmrs.Concept>()));
        }
        return bahmniObservations;
    }

}
