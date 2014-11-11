package org.bahmni.module.bahmnicore.service;

import org.openmrs.Order;

import java.util.List;

public interface OrderService {
    List<Order> getPendingOrders(String patientUuid, String orderTypeUuid);
}
