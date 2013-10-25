package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.ApplicationError;
import org.bahmni.module.bahmnicore.model.BahmniLabResult;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@Service
public class BahmniLabResultServiceImpl implements BahmniLabResultService {

    public static final String LAB_RESULT_OBS_GROUP_CONCEPT_NAME = "Laboratory";
    private EncounterService encounterService;
    private ConceptService conceptService;

    private Concept labResultObsGroupConcept;

    @Autowired
    public BahmniLabResultServiceImpl(EncounterService encounterService, ConceptService conceptService) {
        this.encounterService = encounterService;
        this.conceptService = conceptService;
    }

    @Override
    public void add(BahmniLabResult bahmniLabResult) {
        validate(bahmniLabResult);
        Encounter encounter = encounterService.getEncounterByUuid(bahmniLabResult.getEncounterUuid());

        Set<Order> orders = encounter.getOrders();
        for (Order order : orders) {
            if(order.getConcept().getUuid().equals(bahmniLabResult.getTestUuid())) {
                Concept laboratory = getLabResultObsGroupConcept();
                Obs obsGroup = findOrInitializeObsGroup(encounter, laboratory);
                Obs obs = findExistingObs(obsGroup, order);
                try {
                    if(obs != null) {
                        update(obs, bahmniLabResult);
                    } else {
                        obs = save(bahmniLabResult, order);
                        obsGroup.addGroupMember(obs);
                        encounter.addObs(obsGroup);
                    }
                } catch (ParseException e) {
                    throw new ApplicationError("Error parsing Lab Result: ", e);
                }
            }
        }

        encounterService.saveEncounter(encounter);
    }

    private void validate(BahmniLabResult bahmniLabResult) {
        if(!bahmniLabResult.isValid()) {
            throw new ApplicationError("EncounterUUID and TestUUID should not be empty");
        }
    }

    private Obs findOrInitializeObsGroup(Encounter encounter, Concept concept) {
        for (Obs obs : encounter.getObsAtTopLevel(false)) {
            if(obs.getConcept().equals(concept)){
                return obs;
            }
        }
        Obs obsGroup = new Obs();
        obsGroup.setConcept(concept);
        return obsGroup;
    }

    private Concept getLabResultObsGroupConcept() {
        if(labResultObsGroupConcept == null) {
            labResultObsGroupConcept = conceptService.getConcept(LAB_RESULT_OBS_GROUP_CONCEPT_NAME);
        }
        return labResultObsGroupConcept;
    }

    private Obs save(BahmniLabResult bahmniLabResult, Order order) throws ParseException {
        Obs obs = new Obs();
        obs.setConcept(order.getConcept());
        obs.setOrder(order);
        obs.setComment(bahmniLabResult.getComments());
        obs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
        obs.setValueAsString(bahmniLabResult.getResult());
        return obs;
    }

    private Obs update(Obs existingObs, BahmniLabResult bahmniLabResult) throws ParseException {
        existingObs.setComment(bahmniLabResult.getComments());
        existingObs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
        existingObs.setValueAsString(bahmniLabResult.getResult());
        return existingObs;
    }

    private Obs findExistingObs(Obs obsGroup, Order order) {
        for (Obs obs : getGroupMembers(obsGroup)) {
            if(obs.getOrder().equals(order)) {
                return obs;
            }
        }
        return null;
    }

    private Set<Obs> getGroupMembers(Obs obsGroup) {
        if(obsGroup.getGroupMembers() == null) {
            return new HashSet<>();
        }
        return obsGroup.getGroupMembers();
    }
}
