package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.List;

public interface OrderDao {
    List<Order> getCompletedOrdersFrom(List<Order> orders);
    List<DrugOrder> getPrescribedDrugOrders(Patient patient, Boolean includeActiveVisit, Integer numberOfVisits);
    List<Visit> getVisitsWithOrders(Patient patient, String orderType, Boolean includeActiveVisit, Integer numberOfVisits);
    List<Visit> getVisitsForUUids(String[] visitUuids);

    List<DrugOrder> getPrescribedDrugOrders(List<String> visitUuids);

    List<DrugOrder> getPrescribedDrugOrdersForConcepts(Patient patient, Boolean includeActiveVisit, Integer numberOfVisits, List<Concept> conceptIds);
}
