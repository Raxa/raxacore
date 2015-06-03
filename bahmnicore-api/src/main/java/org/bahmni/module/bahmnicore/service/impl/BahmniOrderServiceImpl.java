package org.bahmni.module.bahmnicore.service.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicore.service.BahmniOrderService;
import org.bahmni.module.bahmnicore.service.OrderService;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.order.contract.BahmniOrder;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class BahmniOrderServiceImpl implements BahmniOrderService {

    private OrderService orderService;
    private BahmniObsService bahmniObsService;
    private static final Logger log = Logger.getLogger(BahmniOrderServiceImpl.class);

    @Autowired
    public BahmniOrderServiceImpl(OrderService orderService, BahmniObsService bahmniObsService) {
        this.orderService = orderService;
        this.bahmniObsService = bahmniObsService;
    }

    @Override
    public List<BahmniOrder> getLatestObservationsAndOrdersForOrderType(String patientUuid, List<Concept> concepts,
                                                                        Integer numberOfVisits, List<String> obsIgnoreList, String orderTypeUuid) {
        List<BahmniOrder> bahmniOrders = new ArrayList<>();
        try {
            List<Order> orders = orderService.getAllOrdersForVisits(patientUuid, orderTypeUuid, numberOfVisits);
            for (Order order : orders) {
                Collection<BahmniObservation> latestObs = bahmniObsService.getLatest(patientUuid, concepts, null,
                        obsIgnoreList, false, order);
                BahmniOrder bahmniOrder = createBahmniOrder(order, latestObs);

                bahmniOrders.add(bahmniOrder);
            }
        }catch (NullPointerException e){
            log.error(" Number of Visits or Order Fields cannot be null");
        }
        return bahmniOrders;
    }

    @Override
    public List<BahmniOrder> getLatestObservationsForOrder(String patientUuid, List<Concept> concepts, List<String> obsIgnoreList, String orderUuid){
        List<BahmniOrder> bahmniOrders = new ArrayList<>();
        Order order = orderService.getOrderByUuid(orderUuid);
        Collection<BahmniObservation> latestObs = bahmniObsService.getLatest(patientUuid, concepts, null, obsIgnoreList, false, order);
        BahmniOrder bahmniOrder = createBahmniOrder(order, latestObs);
        bahmniOrders.add(bahmniOrder);
        return bahmniOrders;
    }

    private BahmniOrder createBahmniOrder(Order order, Collection<BahmniObservation> bahmniObservations){
        BahmniOrder bahmniOrder = new BahmniOrder();

        bahmniOrder.setOrderDateTime(order.getDateActivated());
        bahmniOrder.setOrderTypeUuid(order.getOrderType().getUuid());
        bahmniOrder.setOrderUuid(order.getUuid());
        bahmniOrder.setProvider(order.getOrderer().getName());
        bahmniOrder.setConceptName(order.getConcept().getName().getName());
        bahmniOrder.setBahmniObservations(bahmniObservations);
        return bahmniOrder;
    }
}
