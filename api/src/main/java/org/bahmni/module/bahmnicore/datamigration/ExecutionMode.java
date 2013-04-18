package org.bahmni.module.bahmnicore.datamigration;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.model.BahmniPatient;

public class ExecutionMode {
    private final boolean dataMigrationMode;
    private static Logger logger = Logger.getLogger(ExecutionMode.class);

    private static String existingPatientMessagePart = "already in use by another patient";
    private static String existingCustomerMessagePart = "Customer with id, name already exists";

    public ExecutionMode(String dataMigrationProperty) {
        dataMigrationMode = !(dataMigrationProperty == null || !Boolean.parseBoolean(dataMigrationProperty));
    }

    public void handleOpenERPFailure(RuntimeException e, BahmniPatient bahmniPatient) {
        handleFailure(e, bahmniPatient, existingCustomerMessagePart, "Customer already present:");
    }

    private void handleFailure(RuntimeException e, BahmniPatient bahmniPatient, String alreadyPresentMessage, String messagePrefix) {
        if (!dataMigrationMode) {
            throw e;
        }

        if (e.getMessage().contains(alreadyPresentMessage))
            logger.warn(messagePrefix + bahmniPatient.getPatientIdentifier());
        else
            throw e;
    }

    public void handleSavePatientFailure(RuntimeException e, BahmniPatient bahmniPatient) {
        handleFailure(e, bahmniPatient, existingPatientMessagePart, "Patient already present:");
    }
}