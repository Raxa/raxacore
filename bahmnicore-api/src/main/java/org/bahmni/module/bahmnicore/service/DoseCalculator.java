package org.bahmni.module.bahmnicore.service;

public interface DoseCalculator {

    Double calculateDose(String patientUuid, Double baseDose) throws Exception;
}
