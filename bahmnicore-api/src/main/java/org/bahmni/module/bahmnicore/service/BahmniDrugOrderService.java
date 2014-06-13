package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.openmrs.DrugOrder;

import java.util.Date;
import java.util.List;

public interface BahmniDrugOrderService {
    void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders, String systemUserName);
    List<DrugOrder> getActiveDrugOrders(String patientUuid);
    List<DrugOrder> getPrescribedDrugOrders(String patientUuid, Integer numberOfVisit);

}
