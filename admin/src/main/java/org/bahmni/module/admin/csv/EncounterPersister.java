package org.bahmni.module.admin.csv;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.BahmniPatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm;
import org.bahmni.module.admin.encounter.BahmniEncounterTransactionImportService;
import org.bahmni.module.admin.observation.DiagnosisImportService;
import org.bahmni.module.admin.observation.ObservationImportService;
import org.bahmni.module.admin.visit.VisitMatcher;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public class EncounterPersister implements EntityPersister<EncounterRow> {
    private static final Logger log = Logger.getLogger(EncounterPersister.class);
    public static final String PATIENT_MATCHING_ALGORITHM_DIRECTORY = "/patientMatchingAlgorithm/";

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

    private UserContext userContext;

    private String patientMatchingAlgorithmClassName;

    public void init(UserContext userContext, String patientMatchingAlgorithmClassName){
        this.userContext = userContext;
        this.patientMatchingAlgorithmClassName = patientMatchingAlgorithmClassName;
    }

    @Override
    public RowResult<EncounterRow> validate(EncounterRow encounterRow) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);

            StringBuilder errorMessage = new StringBuilder();

            String messageForInvalidEncounterType = messageForInvalidEncounterType(encounterRow.encounterType);
            if (!StringUtils.isEmpty(messageForInvalidEncounterType))
                errorMessage.append(messageForInvalidEncounterType);

            String messageForInvalidVisitType = messageForInvalidVisitType(encounterRow.visitType);
            if (!StringUtils.isEmpty(messageForInvalidVisitType)) errorMessage.append(messageForInvalidVisitType);

            String messageForInvalidEncounterDate = messageForInvalidEncounterDate(encounterRow);
            if (!StringUtils.isEmpty(messageForInvalidEncounterDate))
                errorMessage.append(messageForInvalidEncounterDate);

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
            if (patient == null) {
                return new RowResult<>(encounterRow, "Patient not found. Patient Id : '" + encounterRow.patientIdentifier + "'");
            }

            VisitMatcher visitMatcher = new VisitMatcher(visitService);
            ObservationImportService observationService = new ObservationImportService(conceptService);
            DiagnosisImportService diagnosisService = new DiagnosisImportService(conceptService);

            BahmniEncounterTransactionImportService encounterTransactionImportService =
                    new BahmniEncounterTransactionImportService(encounterService, visitMatcher, observationService, diagnosisService);
            BahmniEncounterTransaction bahmniEncounterTransaction = encounterTransactionImportService.getBahmniEncounterTransaction(encounterRow, patient);

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

    private String messageForInvalidEncounterDate(EncounterRow encounterRow) {
        try {
            encounterRow.getEncounterDate();
        } catch (ParseException | NullPointerException e) {
            return "Encounter date time is required and should be 'dd/mm/yyyy' format\n";
        }
        return null;
    }

    private String messageForInvalidVisitType(String visitTypeAsString) {
        if (StringUtils.isEmpty(visitTypeAsString)) {
            return "Empty Visit Type";
        }
        List<VisitType> visitTypes = visitService.getVisitTypes(visitTypeAsString);
        if (visitTypes == null || visitTypes.size() == 0) {
            return String.format("Visit Type '%s' not found\n", visitTypeAsString);
        }
        return null;
    }


    private String messageForInvalidEncounterType(String encounterTypeAsString) {
        if (StringUtils.isEmpty(encounterTypeAsString)) {
            return "Empty Encounter Type\n";
        }
        EncounterType encounterType = encounterService.getEncounterType(encounterTypeAsString);
        if (encounterType == null) {
            return String.format("Encounter Type '%s' not found\n", encounterTypeAsString);
        }
        return null;
    }

    private Patient matchPatients(List<Patient> matchingPatients, List<KeyValue> patientAttributes) throws IOException, IllegalAccessException, InstantiationException {
        log.debug("PatientMatching : Start");
        PatientMatchingAlgorithm patientMatchingAlgorithm = new BahmniPatientMatchingAlgorithm();
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(new File(getAlgorithmClassPath()));
            patientMatchingAlgorithm = (PatientMatchingAlgorithm) clazz.newInstance();
        } catch (FileNotFoundException ignored) {
        } finally {
            log.debug("PatientMatching : Using Algorithm in " + patientMatchingAlgorithm.getClass().getName());
            Patient patient = patientMatchingAlgorithm.run(matchingPatients, patientAttributes);
            log.debug("PatientMatching : Done");
            return patient;
        }
    }

    private String getAlgorithmClassPath() {
        return OpenmrsUtil.getApplicationDataDirectory() + PATIENT_MATCHING_ALGORITHM_DIRECTORY + patientMatchingAlgorithmClassName;
    }

}
