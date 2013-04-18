package org.bahmni.datamigration;

import org.bahmni.datamigration.request.patient.PatientRequest;

public class PatientData {
    private PatientRequest patientRequest;
    private Object originalData;

    public PatientData(PatientRequest patientRequest, Object originalData) {
        this.patientRequest = patientRequest;
        this.originalData = originalData;
    }

    public PatientRequest getPatientRequest() {
        return patientRequest;
    }

    public void setPatientRequest(PatientRequest patientRequest) {
        this.patientRequest = patientRequest;
    }

    public Object getOriginalData() {
        return originalData;
    }

    public void setOriginalData(Object originalData) {
        this.originalData = originalData;
    }
}