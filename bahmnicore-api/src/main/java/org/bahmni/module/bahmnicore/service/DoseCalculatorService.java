package org.bahmni.module.bahmnicore.service;

public interface DoseCalculatorService {

    Double calculateDose(String patientUuid, Double baseDose, String doseUnits) throws Exception;
}
