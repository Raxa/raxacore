package org.bahmni.module.bahmnicore.service;

import org.openmrs.Concept;
import org.openmrs.module.bahmniemrapi.order.contract.BahmniOrder;

import java.util.List;

public interface BahmniOrderService {

    List<BahmniOrder> getLatestObservationsAndOrdersForOrderType(String patientUuid, List<Concept> concepts,
                                                                 Integer numberOfVisits, List<String> obsIgnoreList, String orderTypeUuid, Boolean includeObs);

    List<BahmniOrder> getLatestObservationsForOrder(String patientUuid, List<Concept> concepts, List<String> obsIgnoreList, String orderUuid);

    List<BahmniOrder> getInitialObsAndOrdersForOrderType(String patientUuid, List<Concept> concepts, Integer numberOfVisits, List<String> obsIgnoreList, String orderTypeUuid, Boolean includeObs);

    List<BahmniOrder> ordersForOrderType(String patientUuid, List<Concept> concepts, Integer numberOfVisits, List<String> obsIgnoreList, String orderTypeUuid, Boolean includeObs);

    List<BahmniOrder> getInitialForOrder(String patientUuid, List<Concept> concepts, List<String> obsIgnoreList, String orderUuid);

    List<BahmniOrder> ordersForOrder(String patientUuid, List<Concept> concepts, List<String> obsIgnoreList, String orderUuid);
}
