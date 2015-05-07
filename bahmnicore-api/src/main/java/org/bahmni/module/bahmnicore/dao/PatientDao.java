package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.openmrs.Patient;

import java.util.List;

public interface PatientDao {

    public List<PatientResponse> getPatients(String identifier, String name, String localName, String addressFieldName, String addressFieldValue, Integer length, Integer offset, String[] patientAttributes);
    public Patient getPatient(String identifier);
    public List<Patient> getPatients(String partialIdentifier, boolean shouldMatchExactPatientId);
}
