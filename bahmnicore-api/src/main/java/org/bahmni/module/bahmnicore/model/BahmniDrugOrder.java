package org.bahmni.module.bahmnicore.model;

public class BahmniDrugOrder {
    private int numberOfDays;
    private String productUuid;
    private Double quantity;
    private Double dosage;
    private String unit;

    public BahmniDrugOrder() {
    }

    public int getNumberOfDays() {
        if(dosage == 0.0){
            return quantity.intValue();
        }
        return (int) (quantity / dosage);
    }

    public String getProductUuid() {
        return productUuid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getDosage() {
        return dosage;
    }

    public String getUnit() {
        return unit;
    }

    public BahmniDrugOrder(String productUuid, Double dosage, int numberOfDays, Double quantity, String unit) {
        this.numberOfDays = numberOfDays;
        this.productUuid = productUuid;
        this.quantity = quantity;
        this.dosage = dosage;
        this.unit = unit;
    }
}
