package org.openmrs.module.bahmniemrapi.encountertransaction.advice;

import groovy.lang.GroovyClassLoader;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.obscalculator.ObsValueCalculator;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.File;
import java.lang.reflect.Method;

public class BahmniEncounterTransactionUpdateAdvice implements MethodBeforeAdvice {
    
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(new File(OpenmrsUtil.getApplicationDataDirectory() + "obscalculator/BahmniObsValueCalculator.groovy")); // TODO : should be moved to Global Property
        ObsValueCalculator obsValueCalculator = (ObsValueCalculator) clazz.newInstance();
        obsValueCalculator.run((BahmniEncounterTransaction) args[0]);
    }
    
}
