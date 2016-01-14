package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.service.impl.DoseCalculatorFactory.DoseUnit;

public interface DoseCalculatorService {

    Double calculateDose(String patientUuid, Double baseDose, DoseUnit doseUnit) throws Exception;
}
