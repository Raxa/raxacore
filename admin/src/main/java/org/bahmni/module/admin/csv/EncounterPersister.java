package org.bahmni.module.admin.csv;

import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class EncounterPersister implements EntityPersister<EncounterRow> {
    private static final Logger log = Logger.getLogger(EncounterPersister.class);

    @Autowired
    private BahmniPatientService patientService;

    @Autowired
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private VisitService visitService;

    private HashMap<String, EncounterTransaction.Concept> cachedConcepts = new HashMap<>();

    public EncounterPersister() {
    }

    @Override
    public RowResult<EncounterRow> validate(EncounterRow encounterRow) {
        Context.openSession();
        Context.authenticate("admin", "test");
        String errorMessage = null;
        EncounterType encounterType = encounterService.getEncounterType(encounterRow.encounterType);
        List<VisitType> visitTypes = visitService.getVisitTypes(encounterRow.visitType);
        Context.closeSession();
        if (encounterType == null) {
            errorMessage = String.format("Encounter Type %s not found", encounterRow.encounterType);
        } else if (visitTypes == null || visitTypes.size() == 0) {
            errorMessage = String.format("Visit Type %s not found", encounterRow.visitType);
        }
        return new RowResult<>(encounterRow, errorMessage);
    }


    @Override
    public RowResult<EncounterRow> persist(EncounterRow encounterRow) {
        Context.openSession();
        Context.authenticate("admin", "test");
        Exception exception = null;
        try {
            Patient patient = matchPatients(patientService.get(encounterRow.patientIdentifier));
            BahmniEncounterTransaction bahmniEncounterTransaction = getBahmniEncounterTransaction(encounterRow, patient);
            bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
        } catch (Exception e) {
            Context.clearSession();
            log.error(e);
            exception = e;
        } finally {
            Context.flushSession();
            Context.closeSession();
            return new RowResult<>(encounterRow, exception);
        }
    }

    private Patient matchPatients(List<Patient> matchingPatients) {
        if (matchingPatients.size() == 1) {
            return matchingPatients.get(0);
        } else {
            return null;
        }
    }

    private BahmniEncounterTransaction getBahmniEncounterTransaction(EncounterRow encounterRow, Patient patient) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setBahmniDiagnoses(getBahmniDiagnosis(encounterRow.getDiagnoses()));
        bahmniEncounterTransaction.setObservations(getObservations(encounterRow.obsRows));
        bahmniEncounterTransaction.setPatientUuid(patient.getUuid());
        String encounterTypeUUID = encounterService.getEncounterType(encounterRow.encounterType).getUuid();
        bahmniEncounterTransaction.setEncounterTypeUuid(encounterTypeUUID);
        String visitTypeUUID = visitService.getVisitTypes(encounterRow.visitType).get(0).getUuid();
        bahmniEncounterTransaction.setVisitTypeUuid(visitTypeUUID);
        return bahmniEncounterTransaction;
    }

    private List<EncounterTransaction.Observation> getObservations(List<KeyValue> obsRows) {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        if (obsRows != null) {
            for (KeyValue obsRow : obsRows) {
                EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
                Concept concept = conceptService.getConceptByName(obsRow.getKey());
                observation.setConcept(new EncounterTransaction.Concept(concept.getUuid()));
                observation.setValue(obsRow.getValue());
                observations.add(observation);
            }
        }
        return observations;
    }

    private List<BahmniDiagnosisRequest> getBahmniDiagnosis(List<String> diagnoses) {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        for (String diagnosis : diagnoses) {
            EncounterTransaction.Concept diagnosisConcept = getDiagnosisConcept(diagnosis);
            BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
            bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
            bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
            bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
            bahmniDiagnoses.add(bahmniDiagnosisRequest);
        }
        return bahmniDiagnoses;
    }

    private EncounterTransaction.Concept getDiagnosisConcept(String diagnosis) {
        if (!cachedConcepts.containsKey(diagnosis)) {
            Concept diagnosisConcept = conceptService.getConceptByName(diagnosis);
            cachedConcepts.put(diagnosis, getEncounterTransactionConcept(diagnosisConcept));
        }
        return cachedConcepts.get(diagnosis);
    }


    private EncounterTransaction.Concept getEncounterTransactionConcept(Concept diagnosisConcept) {
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        concept.setUuid(diagnosisConcept.getUuid());
        return concept;
    }
}
