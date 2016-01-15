package org.bahmni.module.bahmnicore.service.impl;

public class Dose {
    private double value;
    private DoseUnit doseUnit;

    public Dose(double value, DoseUnit doseUnit) {
        this.value = value;
        this.doseUnit = doseUnit;
    }

    public double getValue() {
        return value;
    }

    public DoseUnit getDoseUnit() {
        return doseUnit;
    }

    public enum CalculatedDoseUnit {
        mg_per_kg ,
        mg_per_m2 ;

        public static CalculatedDoseUnit getConstant(String stringDoseUnit){
            if("mg/kg".equals(stringDoseUnit)) return mg_per_kg;
            if("mg/m2".equals(stringDoseUnit)) return mg_per_m2;
            return null;
        }

        public static DoseUnit getResultantDoseUnit(CalculatedDoseUnit calculatedDoseUnit){
            if(mg_per_kg.equals(calculatedDoseUnit)) return DoseUnit.mg;
            if(mg_per_m2.equals(calculatedDoseUnit)) return DoseUnit.mg;
            return null;
        }
    }

    public enum DoseUnit {
        mg
    }
}
