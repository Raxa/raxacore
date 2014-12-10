package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.drugorder.*;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;

import java.util.Date;
import java.util.List;

public interface BahmniDrugOrderService {
    void add(String patientId, Date orderDate, List<BahmniFeedDrugOrder> bahmniDrugOrders, String systemUserName, String visitTypeName);
    List getActiveDrugOrders(String patientUuid);

    List<DrugOrder> getPrescribedDrugOrders(String patientUuid, Boolean includeActiveVisit, Integer numberOfVisit);

    List<DrugOrder> getPrescribedDrugOrdersForConcepts(Patient patient, Boolean includeActiveVisit, Integer numberOfVisits, List<Concept> concepts);

    DrugOrderConfigResponse getConfig();
}
