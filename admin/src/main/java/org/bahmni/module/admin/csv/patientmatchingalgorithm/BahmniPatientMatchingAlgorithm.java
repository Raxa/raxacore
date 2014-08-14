package org.bahmni.module.admin.csv.patientmatchingalgorithm;

import org.bahmni.csv.KeyValue;
import org.openmrs.Patient;

import java.util.List;

public class BahmniPatientMatchingAlgorithm implements PatientMatchingAlgorithm {
    @Override
    public Patient run(List<Patient> patientList, List<KeyValue> patientAttributes) {
        if (patientList.size() > 0)
            return patientList.get(0);
        return null;
    }
}
