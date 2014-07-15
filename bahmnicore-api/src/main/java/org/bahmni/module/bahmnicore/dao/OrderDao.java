package org.bahmni.module.bahmnicore.dao;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.List;

public interface OrderDao {
    List<Order> getCompletedOrdersFrom(List<Order> orders);
    List<DrugOrder> getActiveDrugOrders(Patient patient);
    List<DrugOrder> getPrescribedDrugOrders(Patient patient, Boolean includeActiveVisit, Integer numberOfVisits);
    public List<Visit> getVisitsWithOrders(Patient patient, String orderType, Boolean includeActiveVisit, Integer numberOfVisits);
}
