package org.bahmni.datamigration;

import org.bahmni.datamigration.request.patient.PatientRequest;

// TODO : Mujir - delete this class.
public class PatientData {
    private PatientRequest patientRequest;

    public PatientData(PatientRequest patientRequest) {
        this.patientRequest = patientRequest;
    }

    public PatientRequest getPatientRequest() {
        return patientRequest;
    }

}