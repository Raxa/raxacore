package org.openmrs.module.bahmniemrapi.builder;

import org.openmrs.TestOrder;

import java.util.Date;
import java.util.UUID;

public class TestOrderBuilder {
    private TestOrder order;

    public TestOrderBuilder() {
        this.order = new TestOrder();
        this.order.setDateCreated(new Date());
        this.order.setUuid(UUID.randomUUID().toString());
        this.order.setDateCreated(new Date());
    }

    public TestOrderBuilder withUuid(UUID uuid) {
        order.setUuid(String.valueOf(uuid));
        return this;
    }

    public TestOrderBuilder withId(Integer id) {
        order.setId(id);
        return this;
    }

    public TestOrder build() {
        return order;
    }
}
