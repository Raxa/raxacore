package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ETObsToBahmniObsMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    public static final String DURATION_CONCEPT_CLASS = "Duration";
    private ConceptService conceptService;

    @Autowired
    public ETObsToBahmniObsMapper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<BahmniObservation> create(List<EncounterTransaction.Observation> allObservations, Date encounterDateTime, String encounterUuid) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : allObservations) {
            bahmniObservations.add(create(observation, encounterDateTime, encounterUuid));
        }
        return bahmniObservations;
    }

    public BahmniObservation create(EncounterTransaction.Observation observation, Date encounterDateTime, String encounterUuid) {
        return map(observation, encounterDateTime,encounterUuid , null,
                Arrays.asList(conceptService.getConceptByUuid(observation.getConceptUuid())),
                false);
    }

    BahmniObservation map(EncounterTransaction.Observation observation, Date encounterDateTime,
                          String encounterUuid, Date visitStartDateTime, List<Concept> rootConcepts, boolean flatten) {
        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setEncounterTransactionObservation(observation);
        bahmniObservation.setEncounterDateTime(encounterDateTime);
        bahmniObservation.setVisitStartDateTime(visitStartDateTime);
        bahmniObservation.setConceptSortWeight(ConceptSortWeightUtil.getSortWeightFor(bahmniObservation.getConcept().getName(), rootConcepts));
        bahmniObservation.setEncounterUuid(encounterUuid);
        if (CONCEPT_DETAILS_CONCEPT_CLASS.equals(observation.getConcept().getConceptClass()) && flatten) {
            for (EncounterTransaction.Observation member : observation.getGroupMembers()) {
                if (member.getVoided()) {
                    continue;
                }
                if (member.getConcept().getConceptClass().equals(ABNORMAL_CONCEPT_CLASS)) {
                    if (member.getValue() instanceof Boolean) {
                        bahmniObservation.setAbnormal((Boolean) member.getValue());
                    } else {
                        bahmniObservation.setAbnormal(Boolean.parseBoolean(((EncounterTransaction.Concept) member.getValue()).getName()));
                    }
                } else if (member.getConcept().getConceptClass().equals(DURATION_CONCEPT_CLASS)) {
                    bahmniObservation.setDuration(new Double(member.getValue().toString()).longValue());
                } else {
                    bahmniObservation.setValue(member.getValue());
                    bahmniObservation.setType(member.getConcept().getDataType());
                }
            }
        } else if (observation.getGroupMembers().size() > 0) {
            for (EncounterTransaction.Observation groupMember : observation.getGroupMembers()) {
                bahmniObservation.addGroupMember(map(groupMember, encounterDateTime,encounterUuid , visitStartDateTime, rootConcepts, flatten));
            }
        } else {
            bahmniObservation.setValue(observation.getValue());
            bahmniObservation.setType(observation.getConcept().getDataType());
        }
        return bahmniObservation;
    }
}
