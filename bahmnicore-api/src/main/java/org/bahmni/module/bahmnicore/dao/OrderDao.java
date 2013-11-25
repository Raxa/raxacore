package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Order;

import java.util.List;

public interface OrderDao {
    List<Order> getCompletedOrdersFrom(List<Order> orders);
}
