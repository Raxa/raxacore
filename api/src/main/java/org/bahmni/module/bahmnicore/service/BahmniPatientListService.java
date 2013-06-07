package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.BahmniPatient;

import java.util.List;

public interface BahmniPatientListService {
    List<BahmniPatient> getAllActivePatients(String location);
}
