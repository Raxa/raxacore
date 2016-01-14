package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.Rule;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    @Autowired
    private ApplicationContext appContext;

    protected static final Log log = LogFactory.getLog(DoseCalculatorServiceImpl.class);

    @Override
    public Double getCalculatedDoseForRule(String patientUuid, Double baseDose, String doseUnits) throws Exception {
        Rule rule = getRule(doseUnits);
        return rule.getDose(patientUuid,baseDose);
    }

    private Rule getRule(String doseUnits) {
        Map<String,Class<? extends Rule>> rulesMapper=new HashMap<String,Class<? extends Rule>>(){{
            this.put("mg/kg", WeightBasedDoseRule.class);
            this.put("mg/m2",BSARule.class);
        }};
        Class<? extends Rule> rule = rulesMapper.get(doseUnits);
        if(null == rule){
            String errMessage = "Dose Calculator for " + doseUnits + " not found";
            throw new APIException(errMessage);
        }
        return appContext.getBean(rule);
    }
}
