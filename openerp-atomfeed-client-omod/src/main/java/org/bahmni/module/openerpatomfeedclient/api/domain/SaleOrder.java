package org.bahmni.module.openerpatomfeedclient.api.domain;

import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.bahmni.module.openerpatomfeedclient.api.util.CustomJsonDateDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;
import java.util.List;

public class SaleOrder {
    private String customerId;
    private String externalId;
    private int id;
    private List<BahmniFeedDrugOrder> saleOrderItems;
    private Date orderDate;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<BahmniFeedDrugOrder> getSaleOrderItems() {
        return saleOrderItems;
    }

    public void setSaleOrderItems(List<BahmniFeedDrugOrder> saleOrderItems) {
        this.saleOrderItems = saleOrderItems;
    }
}
