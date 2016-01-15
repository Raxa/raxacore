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

    public static final Map<Dose.CalculatedDoseUnit, Class<? extends DoseCalculator>> doseCalculatorMap = new HashMap<Dose.CalculatedDoseUnit, Class<? extends DoseCalculator>>() {{
        this.put(Dose.CalculatedDoseUnit.mg_per_kg, WeightBasedDoseCalculator.class);
        this.put(Dose.CalculatedDoseUnit.mg_per_m2, BSABasedDoseCalculator.class);
    }};

    public DoseCalculator getCalculator(Dose.CalculatedDoseUnit calculatedDoseUnit) {
        return appContext.getBean(doseCalculatorMap.get(calculatedDoseUnit));
    }

}