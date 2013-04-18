package org.bahmni.datamigration;

public interface PatientEnumerator {
    PatientData nextPatient() throws Exception;
    void failedPatient(PatientData patientData);
}