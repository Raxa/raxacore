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
    private static final String COMMENTS_CONCEPT_NAME = "COMMENTS";
    private EncounterService encounterService;
    private ConceptService conceptService;

    private Concept labResultObsGroupConcept;
    private Concept commentsConcept;

    @Autowired
    public BahmniLabResultServiceImpl(EncounterService encounterService, ConceptService conceptService) {
        this.encounterService = encounterService;
        this.conceptService = conceptService;
    }

    @Override
    public void add(BahmniLabResult bahmniLabResult) {
        validate(bahmniLabResult);
        Encounter encounter = encounterService.getEncounterByUuid(bahmniLabResult.getEncounterUuid());
        Concept laboratory = getLabResultObsGroupConcept();
        Obs obsGroupLab = findOrInitializeObsGroup(encounter, laboratory);
        Concept test = conceptService.getConceptByUuid(bahmniLabResult.getTestUuid());

        for (Order order : encounter.getOrders()) {
            if(bahmniLabResult.getPanelUuid() != null){
                if(order.getConcept().getUuid().equals(bahmniLabResult.getPanelUuid())){
                    Concept panel = conceptService.getConceptByUuid(bahmniLabResult.getPanelUuid());
                    Obs panelObs = addPanelObs(bahmniLabResult, panel, order, obsGroupLab);
                    Obs testObs = addTestObs(bahmniLabResult, test, order, panelObs);
                    setEncounterObs(encounter, obsGroupLab);
                }
            }
            else if (order.getConcept().getUuid().equals(bahmniLabResult.getTestUuid())) {
                Obs testObs = addTestObs(bahmniLabResult, test, order, obsGroupLab);
                setEncounterObs(encounter, obsGroupLab);
            }
        }

        encounterService.saveEncounter(encounter);
    }

    private void setEncounterObs(Encounter encounter, Obs obs) {
        Set<Obs> encounterObs = encounter.getObs();
        encounterObs.remove(obs);
        encounter.addObs(obs);
    }

    private Obs addPanelObs(BahmniLabResult bahmniLabResult, Concept concept, Order order, Obs parentObsGroup) {
        Obs existingObs = findExistingObs(parentObsGroup, concept);
        if(existingObs == null) {
            Obs obs = new Obs();
            obs.setConcept(concept);
            obs.setOrder(order);
            obs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
            parentObsGroup.addGroupMember(obs);
            return obs;
        }
        return existingObs;
    }

    private Obs addTestObs(BahmniLabResult bahmniLabResult, Concept concept, Order order, Obs parentObsGroup) {
        Obs existingObs = findExistingObs(parentObsGroup, concept);
        if(existingObs == null) {
            return createTestObs(bahmniLabResult, concept, order, parentObsGroup);
        } else {
            return updateTestObs(bahmniLabResult, existingObs);
        }
    }

    private Obs updateTestObs(BahmniLabResult bahmniLabResult, Obs existingObs) {
        try {
            setValue(existingObs, bahmniLabResult, existingObs.getConcept());
            existingObs.setComment(bahmniLabResult.getComments());
            handleNotes(existingObs, bahmniLabResult);
            return existingObs;
        } catch (ParseException e) {
            throw new ApplicationError("Error parsing Lab Result: ", e);
        }
    }

    private Obs createTestObs(BahmniLabResult bahmniLabResult, Concept concept, Order order, Obs parentObsGroup) {
        try {
            Obs obs = new Obs();
            obs.setConcept(concept);
            obs.setOrder(order);
            obs.setComment(bahmniLabResult.getComments());
            obs.setAccessionNumber(bahmniLabResult.getAccessionNumber());
            setValue(obs, bahmniLabResult, concept);
            handleNotes(obs, bahmniLabResult);
            parentObsGroup.addGroupMember(obs);
            return obs;
        } catch (ParseException e) {
            throw new ApplicationError("Error parsing Lab Result: ", e);
        }
    }

    private void setValue(Obs obs, BahmniLabResult bahmniLabResult, Concept concept) throws ParseException {
        if(concept.getDatatype().isCoded()){
            obs.setValueText(bahmniLabResult.getResult());
        }
        else {
            obs.setValueAsString(bahmniLabResult.getResult());
        }
    }

    private void handleNotes(Obs obs, BahmniLabResult bahmniLabResult) {
        for (String note : getNewNotesToBeAdded(obs, bahmniLabResult)) {
            Obs noteObs = new Obs();
            noteObs.setConcept(getCommentsConcept());
            noteObs.setValueText(note);
            obs.addGroupMember(noteObs);
        }
    }

    private HashSet<String> getNewNotesToBeAdded(Obs obs, BahmniLabResult bahmniLabResult) {
        HashSet<String> notes = new HashSet<>(bahmniLabResult.getNotes());
        HashSet<String> existingNotes = new HashSet<>();
        for (Obs note : getGroupMembers(obs)) {
            existingNotes.add(note.getValueText());
        }
        notes.removeAll(existingNotes);
        return notes;
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

    private Concept getCommentsConcept() {
        if(commentsConcept == null) {
            commentsConcept = conceptService.getConcept(COMMENTS_CONCEPT_NAME);
        }
        return commentsConcept;
    }

    private Obs findExistingObs(Obs obsGroup, Concept concept) {
        for (Obs obs : getGroupMembers(obsGroup)) {
            if(obs.getConcept().equals(concept)) {
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
