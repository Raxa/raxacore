package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.mapper.BahmniProviderMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.EncounterDetails;
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

    public List<BahmniObservation> create(List<EncounterTransaction.Observation> allObservations, EncounterDetails encounterDetails) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : allObservations) {
            bahmniObservations.add(create(observation, encounterDetails));
        }
        return bahmniObservations;
    }

    public BahmniObservation create(EncounterTransaction.Observation observation, EncounterDetails encounterDetails) {
        return map(observation,encounterDetails,
                Arrays.asList(conceptService.getConceptByUuid(observation.getConceptUuid())),
                false);
    }

    BahmniObservation map(EncounterTransaction.Observation observation, EncounterDetails encounterDetails, List<Concept> rootConcepts, boolean flatten) {

        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setEncounterTransactionObservation(observation);
        bahmniObservation.setEncounterDateTime(encounterDetails.getEncounterDateTime());
        bahmniObservation.setVisitStartDateTime(encounterDetails.getVisitDateTime());
        bahmniObservation.setConceptSortWeight(ConceptSortWeightUtil.getSortWeightFor(bahmniObservation.getConcept().getName(), rootConcepts));
        bahmniObservation.setEncounterUuid(encounterDetails.getEncounterUuid());
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
                    bahmniObservation.getConcept().setUnits(member.getConcept().getUnits());
                }
            }
        } else if (observation.getGroupMembers().size() > 0) {
            for (EncounterTransaction.Observation groupMember : observation.getGroupMembers()) {
                bahmniObservation.addGroupMember(map(groupMember, encounterDetails, rootConcepts, flatten));
            }
        } else {
            bahmniObservation.setValue(observation.getValue());
            bahmniObservation.setType(observation.getConcept().getDataType());
        }

        for (EncounterTransaction.Provider provider : encounterDetails.getProviders()) {
            bahmniObservation.addProvider(provider);
        }
        return bahmniObservation;
    }

}
