package org.bahmni.module.bahmnicore.service;

public interface DoseCalculator {

    Double getDose(String patientUuid, Double baseDose) throws Exception;
}
