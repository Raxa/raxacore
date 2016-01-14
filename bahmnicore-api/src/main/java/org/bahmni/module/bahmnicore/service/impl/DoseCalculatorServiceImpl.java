package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.DoseCalculator;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bahmni.module.bahmnicore.service.impl.DoseCalculatorFactory.DoseUnit;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    @Autowired
    private DoseCalculatorFactory doseCalculatorFactory;

    @Override
    public Double calculateDose(String patientUuid, Double baseDose, DoseUnit doseUnit) throws Exception {
        DoseCalculator doseCalculator = doseCalculatorFactory.getCalculator(doseUnit);
        return doseCalculator.calculateDose(patientUuid, baseDose);
    }

}
