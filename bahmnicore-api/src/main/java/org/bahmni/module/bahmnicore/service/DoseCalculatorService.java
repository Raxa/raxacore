package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.service.impl.DoseCalculatorFactory.CalculatedDoseUnit;

public interface DoseCalculatorService {

    Double calculateDose(String patientUuid, Double baseDose, CalculatedDoseUnit calculatedDoseUnit) throws Exception;
}
