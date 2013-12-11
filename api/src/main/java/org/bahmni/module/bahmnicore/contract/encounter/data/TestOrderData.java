package org.bahmni.module.bahmnicore.contract.encounter.data;

public class TestOrderData {
    private String conceptUUID;

    public TestOrderData() {
    }

    public TestOrderData(String conceptUUID) {
        this.conceptUUID = conceptUUID;
    }

    public String getConceptUUID() {
        return conceptUUID;
    }

    public void setConceptUUID(String conceptUUID) {
        this.conceptUUID = conceptUUID;
    }
}
