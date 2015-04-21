package org.bahmni.module.bahmnimetadata.contract;

public class ConceptValue {
    private String value;

    public Boolean getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        this.abnormal = abnormal;
    }

    private Boolean abnormal;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
