package org.bahmni.module.admin.csv.persister;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.Messages;
import org.bahmni.module.admin.csv.models.MultipleEncounterRow;
import org.bahmni.module.admin.csv.service.PatientMatchService;
import org.bahmni.module.admin.encounter.BahmniEncounterTransactionImportService;
import org.bahmni.module.admin.retrospectiveEncounter.service.DuplicateObservationService;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EncounterPersister implements EntityPersister<MultipleEncounterRow> {
    public static final String IMPORT_ID = "IMPORT_ID_";
    @Autowired
    private PatientMatchService patientMatchService;
    @Autowired
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;
    @Autowired
    private DuplicateObservationService duplicateObservationService;
    @Autowired
    private BahmniEncounterTransactionImportService bahmniEncounterTransactionImportService;

    private UserContext userContext;
    private String patientMatchingAlgorithmClassName;
    private boolean shouldMatchExactPatientId;
    private String loginUuid;

    private static final Logger log = Logger.getLogger(EncounterPersister.class);

    public void init(UserContext userContext, String patientMatchingAlgorithmClassName, boolean shouldMatchExactPatientId, String loginUuid) {
        this.userContext = userContext;
        this.patientMatchingAlgorithmClassName = patientMatchingAlgorithmClassName;
        this.shouldMatchExactPatientId = shouldMatchExactPatientId;
        this.loginUuid = loginUuid;
    }

    @Override
    public Messages validate(MultipleEncounterRow multipleEncounterRow) {
        return new Messages();
    }

    @Override
    public Messages persist(MultipleEncounterRow multipleEncounterRow) {
        // This validation is needed as patientservice get returns all patients for empty patient identifier
        if (StringUtils.isEmpty(multipleEncounterRow.patientIdentifier)) {
            return noMatchingPatients(multipleEncounterRow);
        }
        synchronized ((IMPORT_ID + multipleEncounterRow.patientIdentifier).intern()) {
            try {
                Context.openSession();
                Context.setUserContext(userContext);

                Patient patient = patientMatchService.getPatient(patientMatchingAlgorithmClassName, multipleEncounterRow.patientAttributes,
                        multipleEncounterRow.patientIdentifier, shouldMatchExactPatientId);
                if (patient == null) {
                    return noMatchingPatients(multipleEncounterRow);
                }

                List<BahmniEncounterTransaction> bahmniEncounterTransactions = bahmniEncounterTransactionImportService.getBahmniEncounterTransaction(multipleEncounterRow, patient);

                for (BahmniEncounterTransaction bahmniEncounterTransaction : bahmniEncounterTransactions) {
                    bahmniEncounterTransaction.setLocationUuid(loginUuid);
                    duplicateObservationService.filter(bahmniEncounterTransaction, patient, multipleEncounterRow.getVisitStartDate(), multipleEncounterRow.getVisitEndDate());
                }

                for (BahmniEncounterTransaction bahmniEncounterTransaction : bahmniEncounterTransactions) {
                    bahmniEncounterTransactionService.save(bahmniEncounterTransaction, patient, multipleEncounterRow.getVisitStartDate(), multipleEncounterRow.getVisitEndDate());
                }

                return new Messages();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Context.clearSession();
                return new Messages(e);
            } finally {
                Context.flushSession();
                Context.closeSession();
            }
        }
    }

    private Messages noMatchingPatients(MultipleEncounterRow multipleEncounterRow) {
        return new Messages("No matching patients found with ID:'" + multipleEncounterRow.patientIdentifier + "'");
    }
}