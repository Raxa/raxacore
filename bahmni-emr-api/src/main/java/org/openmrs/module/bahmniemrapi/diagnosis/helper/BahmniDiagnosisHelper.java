package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BahmniDiagnosisHelper {

    public static final String BAHMNI_DIAGNOSIS_STATUS = "Bahmni Diagnosis Status";
    public static final String BAHMNI_DIAGNOSIS_REVISED = "Bahmni Diagnosis Revised";
    public static final String BAHMNI_INITIAL_DIAGNOSIS = "Bahmni Initial Diagnosis";

    private ObsService obsService;

    private ConceptService conceptService;

    private EmrApiProperties emrApiProperties;

    @Autowired
    public BahmniDiagnosisHelper(ObsService obsService, ConceptService conceptService, EmrApiProperties emrApiProperties) {
        this.obsService = obsService;
        this.conceptService = conceptService;
        this.emrApiProperties = emrApiProperties;
    }

    public void updateDiagnosisMetaData(BahmniDiagnosisRequest bahmniDiagnosis, EncounterTransaction.Diagnosis diagnosis, Encounter encounter) {
        Obs matchingDiagnosisObs = findDiagnosisObsGroup(encounter, diagnosis.getExistingObs());

        updateFirstDiagnosis(matchingDiagnosisObs, bahmniDiagnosis, getBahmniInitialDiagnosisConcept());
        updateStatusConcept(matchingDiagnosisObs, bahmniDiagnosis, getBahmniDiagnosisStatusConcept());
        updateRevisedConcept(matchingDiagnosisObs, getBahmniDiagnosisRevisedConcept());

        matchingDiagnosisObs.setComment(bahmniDiagnosis.getComments());
    }

    private Concept getBahmniDiagnosisRevisedConcept() {
        return conceptService.getConceptByName(BAHMNI_DIAGNOSIS_REVISED);
    }

    private Concept getBahmniDiagnosisStatusConcept() {
        return conceptService.getConceptByName(BAHMNI_DIAGNOSIS_STATUS);
    }

    private Concept getBahmniInitialDiagnosisConcept() {
        return conceptService.getConceptByName(BAHMNI_INITIAL_DIAGNOSIS);
    }

    private void updateFirstDiagnosis(Obs diagnosisObs, BahmniDiagnosisRequest bahmniDiagnosis, Concept bahmniInitialDiagnosis) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniInitialDiagnosis);
        if (bahmniDiagnosis.getPreviousObs() == null && obs.getId() == null) { //Diagnosis captured for first time in this encounter 
            obs.setValueText(diagnosisObs.getUuid());
        } else { //Diagnosis update in the same encounter it was created in AND Diagnosis updated from another encounter
            Obs firstDiagnosisObs = obsService.getObsByUuid(bahmniDiagnosis.getFirstDiagnosis().getExistingObs());
            obs.setValueText(firstDiagnosisObs.getUuid());
        }
        addToObsGroup(diagnosisObs, obs);
    }

    public void markAsRevised(Encounter encounter, String diagnosisObsUUID) {
        Obs diagnosisObs = null;
        for (Obs obs : encounter.getAllObs()) {
            if (obs.getUuid().equals(diagnosisObsUUID)) {
                diagnosisObs = obs;
                break;
            }
        }
        if (diagnosisObs == null)
            throw new AssertionError(String.format("Cannot find revised obs in the diagnosis obs group %s", diagnosisObsUUID));
        Obs revisedObs = findObs(diagnosisObs, BAHMNI_DIAGNOSIS_REVISED);
        revisedObs.setValueBoolean(true);
    }


    private Obs findDiagnosisObsGroup(Encounter encounter, String obsUUID) {
        for (Obs obs : encounter.getAllObs()) {
            if (obs.getUuid().equals(obsUUID)) return obs;
        }
        throw new AssertionError(String.format("Should have found observation %s in encounter %s", obsUUID, encounter.getUuid()));
    }

    private Obs findObs(Obs diagnosisObs, String conceptName) {
        for (Obs o : diagnosisObs.getGroupMembers()) {
            if (o.getConcept().hasName(conceptName, null)) {
                return o;
            }
        }
        throw new AssertionError(String.format("Diagnosis found without meta-data for %s, diagnosisObsUUID: %s", conceptName, diagnosisObs.getUuid()));
    }

    private Obs findOrCreateObs(Obs diagnosisObs, Concept concept) {
        for (Obs o : diagnosisObs.getGroupMembers()) {
            if (concept.equals(o.getConcept())) {
                return o;
            }
        }
        Obs obs = new Obs();
        obs.setConcept(concept);
        return obs;
    }

    private void updateStatusConcept(Obs diagnosisObs, BahmniDiagnosis bahmniDiagnosis, Concept bahmniDiagnosisStatusConcept) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniDiagnosisStatusConcept);
        Concept statusConcept = null;
        if (bahmniDiagnosis.getDiagnosisStatusConcept() != null) {
            statusConcept = conceptService.getConcept(bahmniDiagnosis.getDiagnosisStatusConcept().getName());
        }
        obs.setValueCoded(statusConcept);
        addToObsGroup(diagnosisObs, obs);
    }

    private void updateRevisedConcept(Obs diagnosisObs, Concept bahmniDiagnosisRevisedConcept) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniDiagnosisRevisedConcept);
        obs.setValueBoolean(false);
        addToObsGroup(diagnosisObs, obs);
    }

    private void addToObsGroup(Obs obsGroup, Obs member) {
        member.setPerson(obsGroup.getPerson());
        member.setObsDatetime(obsGroup.getObsDatetime());
        member.setLocation(obsGroup.getLocation());
        member.setEncounter(obsGroup.getEncounter());
        obsGroup.addGroupMember(member);
    }

    public Diagnosis getLatestBasedOnAnyDiagnosis(Diagnosis diagnosis) {
        Obs obs = getLatestObsGroupBasedOnAnyDiagnosis(diagnosis);
        if (obs != null) {
            return buildDiagnosisFromObsGroup(obs);
        }
        return null;
    }

    private Obs getLatestObsGroupBasedOnAnyDiagnosis(Diagnosis diagnosis) {
        String initialDiagnosisUuid = findObs(diagnosis.getExistingObs(), BAHMNI_INITIAL_DIAGNOSIS).getValueText();

        List<Obs> observations = obsService.getObservations(Arrays.asList(diagnosis.getExistingObs().getPerson()), null,
                Arrays.asList(getBahmniDiagnosisRevisedConcept()),
                Arrays.asList(conceptService.getFalseConcept()), null, null, null,
                null, null, null, null, false);

        for (Obs obs : observations) {
            Obs diagnosisObsGroup = obs.getObsGroup();
            //This is main diagosis group. Now, find the initialDiagnosis.  Also ensure that this is visitDiagnosis??
            Obs bahmniInitialDiagnosis = findObs(diagnosisObsGroup, BAHMNI_INITIAL_DIAGNOSIS);
            if (initialDiagnosisUuid.equals(bahmniInitialDiagnosis.getValueText())) {
                return diagnosisObsGroup;
            }
        }

        return null;
    }

    public Diagnosis buildDiagnosisFromObsGroup(Obs diagnosisObsGroup) {
        if (diagnosisObsGroup == null)
            return null;

        Diagnosis diagnosis = emrApiProperties.getDiagnosisMetadata().toDiagnosis(diagnosisObsGroup);

        Collection<Concept> nonDiagnosisConcepts = emrApiProperties.getSuppressedDiagnosisConcepts();
        Collection<Concept> nonDiagnosisConceptSets = emrApiProperties.getNonDiagnosisConceptSets();

        Set<Concept> filter = new HashSet<Concept>();
        filter.addAll(nonDiagnosisConcepts);
        for (Concept conceptSet : nonDiagnosisConceptSets) {
            filter.addAll(conceptSet.getSetMembers());
        }

        if (!filter.contains(diagnosis.getDiagnosis().getCodedAnswer())) {
            return diagnosis;
        }
        return null;
    }

}
