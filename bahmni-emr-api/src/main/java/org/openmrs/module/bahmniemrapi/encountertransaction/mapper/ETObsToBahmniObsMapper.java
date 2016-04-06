package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ETObsToBahmniObsMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    public static final String DURATION_CONCEPT_CLASS = "Duration";
    public static final String UNKNOWN_CONCEPT_CLASS = "Unknown" ;
    private ConceptService conceptService;

    @Autowired
    public ETObsToBahmniObsMapper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<BahmniObservation> create(List<EncounterTransaction.Observation> allObservations, AdditionalBahmniObservationFields additionalBahmniObservationFields) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : allObservations) {
            bahmniObservations.add(create(observation, additionalBahmniObservationFields));
        }
        return bahmniObservations;
    }

    public BahmniObservation create(EncounterTransaction.Observation observation, AdditionalBahmniObservationFields additionalBahmniObservationFields) {
        return map(observation, additionalBahmniObservationFields,
                Collections.singletonList(conceptService.getConceptByUuid(observation.getConceptUuid())),
                false);
    }

    protected BahmniObservation map(EncounterTransaction.Observation observation, AdditionalBahmniObservationFields additionalBahmniObservationFields, List<Concept> rootConcepts, boolean flatten) {

        BahmniObservation bahmniObservation= createBahmniObservation(observation,additionalBahmniObservationFields,rootConcepts,flatten);

        if (CONCEPT_DETAILS_CONCEPT_CLASS.equals(observation.getConcept().getConceptClass()) && flatten) {
            handleFlattenedConceptDetails(observation,bahmniObservation);
        } else if (observation.getGroupMembers().size() > 0) {
            for (EncounterTransaction.Observation groupMember : observation.getGroupMembers()) {
                AdditionalBahmniObservationFields additionalFields = (AdditionalBahmniObservationFields) additionalBahmniObservationFields.clone();
                additionalFields.setObsGroupUuid(observation.getUuid());
                bahmniObservation.addGroupMember(map(groupMember, additionalFields, rootConcepts, flatten));
            }
        } else {
            bahmniObservation.setValue(observation.getValue());
            bahmniObservation.setType(observation.getConcept().getDataType());
            bahmniObservation.setHiNormal(observation.getConcept().getHiNormal());
            bahmniObservation.setLowNormal(observation.getConcept().getLowNormal());
        }

        for (EncounterTransaction.Provider provider : additionalBahmniObservationFields.getProviders()) {
            bahmniObservation.addProvider(provider);
        }
        if (observation.getCreator() != null) {
            bahmniObservation.setCreatorName(observation.getCreator().getPersonName());
        }
        return bahmniObservation;
    }

    private void setValueAndType(BahmniObservation bahmniObservation, EncounterTransaction.Observation member) {
        if(!bahmniObservation.isUnknown()) {
            bahmniObservation.setValue(member.getValue());
            bahmniObservation.setType(member.getConcept().getDataType());
        }
    }

    private void handleUnknownConceptClass(BahmniObservation bahmniObservation, EncounterTransaction.Observation etObs) {
        Object unknownObsValue = etObs.getValue();
        if(!(unknownObsValue instanceof Boolean) && unknownObsValue == null){
            return;
        }

        bahmniObservation.setUnknown((Boolean)unknownObsValue);
        if((Boolean)unknownObsValue){
            bahmniObservation.setValue(getConceptName(etObs));
        }
    }

    private String getConceptName(EncounterTransaction.Observation etObs) {
        return etObs.getConcept().getShortName() != null ? etObs.getConcept().getShortName() : etObs.getConcept().getName();
    }
    private void handleAbnormalConceptClass(BahmniObservation bahmniObservation, EncounterTransaction.Observation etObs) {
        if (etObs.getValue() instanceof Boolean) {
            bahmniObservation.setAbnormal((Boolean) etObs.getValue());
        } else {
            if (etObs.getValue() != null) {
                bahmniObservation.setAbnormal(Boolean.parseBoolean(((EncounterTransaction.Concept) etObs.getValue()).getName()));
            }
        }
    }

    private void handleFlattenedConceptDetails(EncounterTransaction.Observation observation, BahmniObservation bahmniObservation) {
        setHiNormalAndLowNormalForNumericUnknownObs(observation, bahmniObservation);
        for (EncounterTransaction.Observation member : observation.getGroupMembers()) {
            if (member.getVoided()) {
                continue;
            }
            if (member.getConcept().getConceptClass().equals(ABNORMAL_CONCEPT_CLASS)) {
                handleAbnormalConceptClass(bahmniObservation, member);
            } else if (member.getConcept().getConceptClass().equals(UNKNOWN_CONCEPT_CLASS)) {
                handleUnknownConceptClass(bahmniObservation, member);
            } else if (member.getConcept().getConceptClass().equals(DURATION_CONCEPT_CLASS)) {
                bahmniObservation.setDuration(new Double(member.getValue().toString()).longValue());
            } else {
                setValueAndType(bahmniObservation, member);
                bahmniObservation.getConcept().setUnits(member.getConcept().getUnits());
                bahmniObservation.setHiNormal(member.getConcept().getHiNormal());
                bahmniObservation.setLowNormal(member.getConcept().getLowNormal());
            }
        }
    }

    private void setHiNormalAndLowNormalForNumericUnknownObs(EncounterTransaction.Observation observation, BahmniObservation bahmniObservation) {
        if (observation.getGroupMembers().size() == 1 && observation.getGroupMembers().get(0).getConcept().getConceptClass().equals(UNKNOWN_CONCEPT_CLASS)){
            Concept conceptDetailsConcept = conceptService.getConceptByUuid(observation.getConceptUuid());

            Concept primaryNumericConcept = conceptDetailsConcept.getSetMembers().get(0);
            if (primaryNumericConcept.isNumeric()){
                ConceptNumeric conceptNumeric = conceptService.getConceptNumeric(primaryNumericConcept.getId());
                bahmniObservation.setHiNormal(conceptNumeric.getHiNormal());
                bahmniObservation.setLowNormal(conceptNumeric.getLowNormal());
            }
        }
    }

    private BahmniObservation createBahmniObservation(EncounterTransaction.Observation observation, AdditionalBahmniObservationFields additionalBahmniObservationFields, List<Concept> rootConcepts, boolean flatten) {
        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setEncounterTransactionObservation(observation);
        bahmniObservation.setEncounterDateTime(additionalBahmniObservationFields.getEncounterDateTime());
        bahmniObservation.setVisitStartDateTime(additionalBahmniObservationFields.getVisitDateTime());
        bahmniObservation.setConceptSortWeight(ConceptSortWeightUtil.getSortWeightFor(bahmniObservation.getConcept().getName(), rootConcepts));
        bahmniObservation.setEncounterUuid(additionalBahmniObservationFields.getEncounterUuid());
        bahmniObservation.setObsGroupUuid(additionalBahmniObservationFields.getObsGroupUuid());
        bahmniObservation.setUnknown(false);
        return bahmniObservation;
    }
}
