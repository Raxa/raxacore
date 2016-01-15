package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.DoseCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DoseCalculatorFactory {

    @Autowired
    private ApplicationContext appContext;

    public static final Map<CalculatedDoseUnit, Class<? extends DoseCalculator>> doseCalculatorMap = new HashMap<CalculatedDoseUnit, Class<? extends DoseCalculator>>() {{
        this.put(CalculatedDoseUnit.mg_per_kg, WeightBasedDoseCalculator.class);
        this.put(CalculatedDoseUnit.mg_per_m2, BSABasedDoseCalculator.class);
    }};

    public DoseCalculator getCalculator(CalculatedDoseUnit calculatedDoseUnit) {
        return appContext.getBean(doseCalculatorMap.get(calculatedDoseUnit));
    }

    public enum CalculatedDoseUnit {
        mg_per_kg ,
        mg_per_m2 ;

        public static CalculatedDoseUnit getConstant(String stringDoseUnit){
            if("mg/kg".equals(stringDoseUnit)) return mg_per_kg;
            if("mg/m2".equals(stringDoseUnit)) return mg_per_m2;
            return null;
        }
    }
}