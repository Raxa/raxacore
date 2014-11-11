package org.bahmni.module.bahmnicore.service;

import org.openmrs.DrugOrder;
import org.openmrs.Order;

import java.util.List;

public interface BahmniOrderService {
    List<Order> getPendingOrders(String patientUuid, String orderTypeUuid);

    List<DrugOrder> getScheduledDrugOrders(String patientUuid);
}
