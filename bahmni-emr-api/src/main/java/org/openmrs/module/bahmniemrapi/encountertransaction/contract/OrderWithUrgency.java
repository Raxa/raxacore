package org.openmrs.module.bahmniemrapi.encountertransaction.contract;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class OrderWithUrgency extends EncounterTransaction.Order {
    private String urgency;

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
}
