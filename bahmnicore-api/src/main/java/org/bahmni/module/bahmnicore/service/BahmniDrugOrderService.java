package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.openmrs.DrugOrder;
import org.openmrs.Order;

import java.util.Date;
import java.util.List;

public interface BahmniDrugOrderService {
    void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders, String systemUserName);
    List getActiveDrugOrders(String patientUuid);

    List<? extends Order> getActiveDrugOrders(String patientUuid, Date asOfDate);

    List<DrugOrder> getPrescribedDrugOrders(String patientUuid, Boolean includeActiveVisit, Integer numberOfVisit);

}
