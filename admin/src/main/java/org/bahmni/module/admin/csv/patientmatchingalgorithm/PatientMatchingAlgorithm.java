package org.bahmni.module.admin.csv.patientmatchingalgorithm;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.exception.CannotMatchPatientException;
import org.openmrs.Patient;

import java.util.List;

public interface PatientMatchingAlgorithm {
    public Patient run(List<Patient> patientList, List<KeyValue> patientAttributes) throws CannotMatchPatientException;
}
