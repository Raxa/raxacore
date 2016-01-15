package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.service.impl.Dose;

public interface DoseCalculator {

    Dose calculateDose(String patientUuid, Double baseDose) throws Exception;
}
