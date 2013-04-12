package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.Patient;

public interface BahmniPatientService {
    public Patient createPatient(BahmniPatient bahmniPatient);
    public Patient updatePatient(BahmniPatient bahmniPatient);
}
