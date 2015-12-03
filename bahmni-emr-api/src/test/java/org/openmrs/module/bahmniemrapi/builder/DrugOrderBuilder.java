package org.openmrs.module.bahmniemrapi.builder;

import org.openmrs.Drug;
import org.openmrs.DrugOrder;

import java.util.UUID;

public class DrugOrderBuilder {
    private DrugOrder order;

    public DrugOrderBuilder() {
        this.order = new DrugOrder();
        this.order.setUuid(UUID.randomUUID().toString());
        this.order.setDateCreated(null);
        this.order.setDrug(new Drug(123));
    }

    public DrugOrderBuilder withUuid(UUID uuid) {
        order.setUuid(String.valueOf(uuid));
        return this;
    }

    public DrugOrderBuilder withId(Integer id) {
        order.setId(id);
        return this;
    }

    public DrugOrder build() {
        return order;
    }
}
