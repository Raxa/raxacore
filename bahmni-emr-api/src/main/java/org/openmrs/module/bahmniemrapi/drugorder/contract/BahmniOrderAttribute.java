package org.openmrs.module.bahmniemrapi.drugorder.contract;

public class BahmniOrderAttribute {
    public static final String ORDER_ATTRIBUTES_CONCEPT_NAME= "Order Attributes";
    private String name;
    private String value;

    public BahmniOrderAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
