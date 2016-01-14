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

    public static final Map<DoseUnit, Class<? extends DoseCalculator>> doseCalculatorMap = new HashMap<DoseUnit, Class<? extends DoseCalculator>>() {{
        this.put(DoseUnit.mg_per_kg, WeightBasedDoseCalculator.class);
        this.put(DoseUnit.mg_per_m2, BSABasedDoseCalculator.class);
    }};

    public DoseCalculator getCalculator(DoseUnit doseUnit) {
        return appContext.getBean(doseCalculatorMap.get(doseUnit));
    }

    public enum DoseUnit {
        mg_per_kg ,
        mg_per_m2 ;

        public static DoseUnit getConstant(String stringDoseUnit){
            if("mg/kg".equals(stringDoseUnit)) return DoseUnit.mg_per_kg;
            if("mg/m2".equals(stringDoseUnit)) return DoseUnit.mg_per_m2;
            return null;
        }
    }
}