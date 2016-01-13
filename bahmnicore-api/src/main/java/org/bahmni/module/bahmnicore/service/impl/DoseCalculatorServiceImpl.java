package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.Rule;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DoseCalculatorServiceImpl implements DoseCalculatorService {

    private final String PACKAGE_PATH = "org.bahmni.module.bahmnicore.service.impl.";
    @Autowired
    private ApplicationContext appContext;

    protected static final Log log = LogFactory.getLog(DoseCalculatorServiceImpl.class);

    @Override
    public Double getCalculatedDoseForRule(String patientUuid, Double baseDose, String ruleName) throws Exception {
        Rule rule = getRule(ruleName);
        return rule.getDose(patientUuid,baseDose);
    }

    private Rule getRule(String ruleName) {
        Class ruleClass;
        try {
            ruleClass = Class.forName(PACKAGE_PATH + ruleName + "Rule");
        } catch (ClassNotFoundException e) {
            String errMessage = "Rule " + ruleName + " not found";
            log.error(errMessage);
            throw new APIException(errMessage);
        }
        return (Rule)appContext.getBean(ruleClass);
    }
}
