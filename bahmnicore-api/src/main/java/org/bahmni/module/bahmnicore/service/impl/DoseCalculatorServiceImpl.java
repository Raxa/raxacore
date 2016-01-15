package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.DoseCalculator;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bahmni.module.bahmnicore.service.impl.Dose.CalculatedDoseUnit;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    @Autowired
    private DoseCalculatorFactory doseCalculatorFactory;

    @Override
    public Dose calculateDose(String patientUuid, Double baseDose, CalculatedDoseUnit calculatedDoseUnit) throws Exception {
        DoseCalculator doseCalculator = doseCalculatorFactory.getCalculator(calculatedDoseUnit);
        return doseCalculator.calculateDose(patientUuid, baseDose);
    }

}
