package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.service.impl.Dose;
import org.bahmni.module.bahmnicore.service.impl.Dose.CalculatedDoseUnit;

public interface DoseCalculatorService {

    Dose calculateDose(String patientUuid, Double baseDose, CalculatedDoseUnit calculatedDoseUnit) throws Exception;
}
