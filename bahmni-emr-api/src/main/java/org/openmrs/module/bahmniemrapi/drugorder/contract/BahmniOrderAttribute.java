package org.openmrs.module.bahmniemrapi.drugorder.contract;

public class BahmniOrderAttribute {
    public static final String ORDER_ATTRIBUTES_CONCEPT_NAME= "Order Attributes";
    private String name;
    private String value;
    private String uuid;

    public BahmniOrderAttribute() {
    }

    public BahmniOrderAttribute(String name, String value, String uuid) {
        this.name = name;
        this.value = value;
        this.uuid = uuid;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
