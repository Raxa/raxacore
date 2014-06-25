package org.bahmni.module.elisatomfeedclient.api.worker;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;

import java.util.List;
import java.util.Set;

public class OrdersHelper {

    private OrderService orderService;
    private ConceptService conceptService;
    private EncounterService encounterService;

    public OrdersHelper(OrderService orderService, ConceptService conceptService, EncounterService encounterService) {
        this.orderService = orderService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
    }

    public Order createOrderInEncounter(Encounter orderEncounter, String orderType, String conceptName) {
        Order labManagerNoteOrder = new Order();

        Concept concept = conceptService.getConcept(conceptName);
        labManagerNoteOrder.setConcept(concept);
        labManagerNoteOrder.setOrderType(getOrderTypeByName(orderType));
        labManagerNoteOrder.setPatient(orderEncounter.getPatient());
        labManagerNoteOrder.setAccessionNumber(orderEncounter.getUuid());
        orderEncounter.addOrder(labManagerNoteOrder);
        encounterService.saveEncounter(orderEncounter);
        return labManagerNoteOrder;
    }

    private OrderType getOrderTypeByName(String orderTypeName) {
        List<OrderType> allOrderTypes = orderService.getOrderTypes(true);
        for (OrderType orderType : allOrderTypes) {
            if (orderType.getName().equals(orderTypeName)) {
                return orderType;
            }
        }
        return null;
    }

    public Order getOrderByConceptName(Encounter orderEncounter, String conceptName) {
        Set<Order> orders = orderEncounter.getOrders();
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                if (order.getConcept() != null && order.getConcept().getName() != null && order.getConcept().getName().getName().equals(conceptName)) {
                    return order;
                }
            }
        }
        return null;
    }

}
