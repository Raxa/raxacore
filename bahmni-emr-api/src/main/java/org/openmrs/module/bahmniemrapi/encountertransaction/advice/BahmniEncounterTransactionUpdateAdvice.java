package org.openmrs.module.bahmniemrapi.encountertransaction.advice;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class BahmniEncounterTransactionUpdateAdvice implements MethodBeforeAdvice {
    
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("for the heck of it");
    }
    
}
