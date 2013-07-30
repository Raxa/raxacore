package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;

import java.util.List;

public interface BahmniPatientDao {

    List<PatientResponse> getPatients(String identifier, String name, String village, Integer length);

}
