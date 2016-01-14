package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.DoseCalculator;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    @Autowired
    private DoseCalculatorFactory doseCalculatorFactory;

    @Override
    public Double calculateDose(String patientUuid, Double baseDose, String doseUnits) throws Exception {
        DoseCalculator doseCalculator = doseCalculatorFactory.getRule(doseUnits);
        return doseCalculator.calculateDose(patientUuid, baseDose);
    }

}
