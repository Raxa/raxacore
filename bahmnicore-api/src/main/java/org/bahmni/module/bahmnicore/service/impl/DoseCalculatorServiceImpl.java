package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    @Autowired
    private RuleFactory ruleFactory;

    @Override
    public Double getCalculatedDoseForRule(String patientUuid, Double baseDose, String doseUnits) throws Exception {
        Rule rule = ruleFactory.getRule(doseUnits);
        return rule.getDose(patientUuid,baseDose);
    }

}
