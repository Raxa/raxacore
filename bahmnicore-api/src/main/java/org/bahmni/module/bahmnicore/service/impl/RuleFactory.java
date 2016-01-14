package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.Rule;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RuleFactory {

    @Autowired
    private ApplicationContext appContext;
    private Map<String, Class<? extends Rule>> rulesMapper;

    public RuleFactory() {
        rulesMapper = new HashMap<String, Class<? extends Rule>>() {{
            this.put("mg/kg", WeightBasedDoseRule.class);
            this.put("mg/m2", BSARule.class);
        }};
    }

    public Rule getRule(String doseUnits) {
        Class<? extends Rule> rule = rulesMapper.get(doseUnits);
        if (null == rule) {
            String errMessage = "Dose Calculator for " + doseUnits + " not found";
            throw new APIException(errMessage);
        }
        return appContext.getBean(rule);
    }
}