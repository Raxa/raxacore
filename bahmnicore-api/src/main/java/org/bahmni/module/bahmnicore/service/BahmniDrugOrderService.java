package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.drugorder.*;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.openmrs.DrugOrder;
import org.openmrs.Order;

import java.util.Date;
import java.util.List;

public interface BahmniDrugOrderService {
    void add(String patientId, Date orderDate, List<BahmniFeedDrugOrder> bahmniDrugOrders, String systemUserName);
    List getActiveDrugOrders(String patientUuid);

    List<? extends Order> getActiveDrugOrders(String patientUuid, Date asOfDate);

    List<DrugOrder> getPrescribedDrugOrders(String patientUuid, Boolean includeActiveVisit, Integer numberOfVisit);

    DrugOrderConfigResponse getConfig();
}
