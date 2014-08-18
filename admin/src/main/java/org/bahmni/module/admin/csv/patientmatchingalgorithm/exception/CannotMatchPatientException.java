package org.bahmni.module.admin.csv.patientmatchingalgorithm.exception;

import org.openmrs.Patient;

import java.util.List;

public class CannotMatchPatientException extends Exception {
    private List<Patient> patients;

    public CannotMatchPatientException() {
    }

    public CannotMatchPatientException(List<Patient> patients) {
        this.patients = patients;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return "CannnotMatchPatientException{" +
                "patients=" + getPatientIds(patients) +
                '}';
    }

    private String getPatientIds(List<Patient> patients) {
        StringBuffer patientIds = new StringBuffer();
        for (Patient patient : patients) {
            patientIds.append(patient.getPatientIdentifier().getIdentifier()).append(", ");
        }
        return patientIds.toString();
    }
}
