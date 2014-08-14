package org.bahmni.module.admin.csv;

import groovy.lang.GroovyClassLoader;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.BahmniPatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
    private UserContext userContext;

    @Override
    public RowResult<EncounterRow> validate(EncounterRow encounterRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);

            EncounterType encounterType = encounterService.getEncounterType(encounterRow.encounterType);
            List<VisitType> visitTypes = visitService.getVisitTypes(encounterRow.visitType);

            StringBuilder errorMessage = new StringBuilder();
            if (encounterType == null) {
                errorMessage.append(String.format("Encounter Type %s not found\n", encounterRow.encounterType));
            }
            if (visitTypes == null || visitTypes.size() == 0) {
                errorMessage.append(String.format("Visit Type %s not found\n", encounterRow.visitType));
            }

            try {
                encounterRow.getEncounterDate();
            } catch (ParseException | NullPointerException e) {
                errorMessage.append("Encounter date time is required and should be 'dd/mm/yyyy' format\n");
            }

            return new RowResult<>(encounterRow, errorMessage.toString());
        } finally {
            Context.closeSession();
        }
    }

    @Override
    public RowResult<EncounterRow> persist(EncounterRow encounterRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);

            Patient patient = matchPatients(patientService.get(encounterRow.patientIdentifier), encounterRow.patientAttributes);
            BahmniEncounterTransaction bahmniEncounterTransaction = getBahmniEncounterTransaction(encounterRow, patient);
            bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
            return new RowResult<>(encounterRow);
        } catch (Exception e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(encounterRow, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
    }

    private Patient matchPatients(List<Patient> matchingPatients, List<KeyValue> patientAttributes) throws IOException, IllegalAccessException, InstantiationException {
        log.info("PatientMatching : Start");
        PatientMatchingAlgorithm patientMatchingAlgorithm = new BahmniPatientMatchingAlgorithm();
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(new File(OpenmrsUtil.getApplicationDataDirectory() + "/patientMatchingAlgorithm/BahmniPatientMatchingAlgorithm.groovy"));
            patientMatchingAlgorithm = (PatientMatchingAlgorithm) clazz.newInstance();
        } catch (FileNotFoundException ignored) {
        } finally {
            log.info("PatientMatching : Using Algorithm in " + patientMatchingAlgorithm.getClass().getName());
            Patient patient = patientMatchingAlgorithm.run(matchingPatients, patientAttributes);
            log.info("PatientMatching : Done");
            return patient;
        }
    }

    private BahmniEncounterTransaction getBahmniEncounterTransaction(EncounterRow encounterRow, Patient patient) throws ParseException {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setBahmniDiagnoses(getBahmniDiagnosis(encounterRow));
        bahmniEncounterTransaction.setObservations(getObservations(encounterRow));
        bahmniEncounterTransaction.setPatientUuid(patient.getUuid());
        bahmniEncounterTransaction.setEncounterDateTime((encounterRow.getEncounterDate()));
        String encounterTypeUUID = encounterService.getEncounterType(encounterRow.encounterType).getUuid();
        bahmniEncounterTransaction.setEncounterTypeUuid(encounterTypeUUID);
        String visitTypeUUID = visitService.getVisitTypes(encounterRow.visitType).get(0).getUuid();
        bahmniEncounterTransaction.setVisitTypeUuid(visitTypeUUID);
        return bahmniEncounterTransaction;
    }

    private List<EncounterTransaction.Observation> getObservations(EncounterRow encounterRow) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();

        List<KeyValue> obsRows = encounterRow.obsRows;
        if (obsRows != null && !obsRows.isEmpty()) {
            for (KeyValue obsRow : obsRows) {
                Concept concept = conceptService.getConceptByName(obsRow.getKey());

                EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
                observation.setConcept(new EncounterTransaction.Concept(concept.getUuid()));
                observation.setValue(obsRow.getValue());
                observation.setObservationDateTime(encounterRow.getEncounterDate());
                observations.add(observation);
            }
        }
        return observations;
    }

    private List<BahmniDiagnosisRequest> getBahmniDiagnosis(EncounterRow encounterRow) throws ParseException {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();

        List<String> diagnoses = encounterRow.getDiagnoses();
        for (String diagnosis : diagnoses) {
            EncounterTransaction.Concept diagnosisConcept = getDiagnosisConcept(diagnosis);
            BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
            bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
            bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
            bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
            bahmniDiagnosisRequest.setDiagnosisDateTime(encounterRow.getEncounterDate());
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

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }
}
