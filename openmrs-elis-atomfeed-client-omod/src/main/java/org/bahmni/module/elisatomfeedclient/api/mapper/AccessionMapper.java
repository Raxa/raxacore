package org.bahmni.module.elisatomfeedclient.api.mapper;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;

import java.util.*;

public class AccessionMapper {
    private final EncounterService encounterService;
    private final PatientService patientService;
    private final VisitService visitService;
    private final ConceptService conceptService;
    private User labUser;
    private OrderService orderService;
    private ElisAtomFeedProperties properties;
    private final UserService userService;
    private final ProviderService providerService;
    private OrderType labOrderType;

    public AccessionMapper(ElisAtomFeedProperties properties) {
        this(Context.getService(EncounterService.class), Context.getService(PatientService.class), Context.getService(VisitService.class), Context.getService(ConceptService.class), Context.getService(UserService.class), Context.getService(ProviderService.class),Context.getService(OrderService.class), properties);
    }

    AccessionMapper(EncounterService encounterService, PatientService patientService, VisitService visitService, ConceptService conceptService, UserService userService, ProviderService providerService, OrderService orderService, ElisAtomFeedProperties properties) {
        this.encounterService = encounterService;
        this.patientService = patientService;
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.orderService = orderService;
        this.properties = properties;
        this.userService = userService;
        this.providerService = providerService;

    }

    public Encounter mapToNewEncounter(OpenElisAccession openElisAccession) {
        Patient patient = patientService.getPatientByUuid(openElisAccession.getPatientUuid());
        if (labUser == null) {
            labUser = userService.getUserByUsername(properties.getLabSystemUserName());
        }

        Provider labSystemProvider = getLabSystemProvider();
        EncounterType encounterType = encounterService.getEncounterType(properties.getEncounterTypeClinical());

        Visit visit = getNearestVisit(patient, openElisAccession);
        checkAndUpdateVisitEndDatetime(visit, openElisAccession.fetchDate());

        Encounter encounter = newEncounterInstance(openElisAccession, visit, patient, labSystemProvider, encounterType);
        encounter.setUuid(openElisAccession.getAccessionUuid());

        Set<String> groupedOrders = groupOrders(openElisAccession.getTestDetails());

        Set<Order> orders = createOrders(openElisAccession, groupedOrders, patient);
        addOrdersToEncounter(encounter, orders);

        return encounter;
    }

    private Encounter newEncounterInstance(OpenElisAccession openElisAccession, Visit visit, Patient patient, Provider labSystemProvider, EncounterType encounterType) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(encounterType);
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(openElisAccession.fetchDate());
        EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        encounter.setProvider(encounterRole, labSystemProvider);
        encounter.setVisit(visit);
        return encounter;
    }

    public Encounter getEncounterForTestResult(OpenElisAccession openElisAccession, Visit accessionVisit, String providerUuid) {
        EncounterType labResultEncounterType = encounterService.getEncounterType("LAB_RESULT");
        //TODO: if the providerUuid is blank, enter as default provider? admin
        Provider provider = providerService.getProviderByUuid(providerUuid);
        List<Encounter> labResultEncounters = encounterService.getEncounters(null, null, null, null, null,
                new HashSet<>(Arrays.asList(labResultEncounterType)), new HashSet<>(Arrays.asList(provider)),
                null, new HashSet<>(Arrays.asList(accessionVisit)), false);

        if (labResultEncounters.size() > 0) {
            return labResultEncounters.get(0);
        }
        else {
            return newEncounterInstance(openElisAccession, accessionVisit, accessionVisit.getPatient(), provider, labResultEncounterType);
        }
    }


    private void checkAndUpdateVisitEndDatetime(Visit visit, Date accessionDatetime) {
        if (visit.getStopDatetime() != null && visit.getStopDatetime().before(accessionDatetime)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(accessionDatetime);
            calendar.add(Calendar.HOUR, 1);
            visit.setStopDatetime(calendar.getTime());
        }
    }

    public Encounter mapToExistingEncounter(OpenElisAccession openElisAccession, AccessionDiff diff, Encounter previousEncounter) {
        if (diff.getAddedTestDetails().size() > 0) {
            Set<String> addedOrders = groupOrders(diff.getAddedTestDetails());
            Set<Order> newOrders = createOrders(openElisAccession, addedOrders, previousEncounter.getPatient());
            addOrdersToEncounter(previousEncounter, newOrders);
        }

        if (diff.getRemovedTestDetails().size() > 0) {
            Set<String> removedOrders = groupOrders(diff.getRemovedTestDetails());
            removeOrders(previousEncounter, removedOrders);
        }

        return previousEncounter;
    }

    private void addOrdersToEncounter(Encounter encounter, Set<Order> orders) {
        for (Order order : orders) {
            encounter.addOrder(order);
        }
    }

    private Set<Order> createOrders(OpenElisAccession openElisAccession, Set<String> orderConceptUuids, Patient patient) {
        Set<Order> orders = new HashSet<>();
        if (labUser == null) {
            labUser = userService.getUserByUsername(properties.getLabSystemUserName());
        }
        OrderType orderType = getLabOrderType();
        for (String orderConceptUuid : orderConceptUuids) {
            TestOrder order = new TestOrder();
            order.setConcept(conceptService.getConceptByUuid(orderConceptUuid));
            order.setAccessionNumber(openElisAccession.getAccessionUuid());
            order.setCreator(labUser);
            order.setPatient(patient);
            order.setOrderType(orderType);
            orders.add(order);
        }
        return orders;
    }

    private OrderType getLabOrderType() {
        if(labOrderType == null){
            List<OrderType> orderTypes = orderService.getAllOrderTypes();
            for (OrderType orderType : orderTypes) {
                if (orderType.getName().equals(properties.getOrderTypeLabOrderName())){
                    labOrderType = orderType;
                    break;
                }
            }
        }
        return labOrderType;
    }

    private void removeOrders(Encounter previousEncounter, Set<String> removedOrders) {
        for (String removedOrder : removedOrders) {
            for (Order order : previousEncounter.getOrders()) {
                if (removedOrder.equals(order.getConcept().getUuid())) {
                    order.setVoided(true);
                }
            }
        }
    }

    private Set<String> groupOrders(Set<OpenElisTestDetail> openElisAccessionTestDetails) {
        Set<String> orderConceptUuids = new HashSet<>();
        for (OpenElisTestDetail testDetail : openElisAccessionTestDetails) {
            String uuid = null;
            if (testDetail.getPanelUuid() != null) {
                uuid = testDetail.getPanelUuid();
            } else {
                uuid = testDetail.getTestUuid();
            }
            orderConceptUuids.add(uuid);
        }
        return orderConceptUuids;
    }

    private Provider getLabSystemProvider() {
        Collection<Provider> labSystemProviders = providerService.getProvidersByPerson(labUser.getPerson());
        return labSystemProviders == null ? null : labSystemProviders.iterator().next();
    }

    private Visit getNearestVisit(Patient patient, OpenElisAccession accession) {
        List<Visit> visits = visitService.getVisits(null, Arrays.asList(patient), null, null, null, accession.fetchDate(), null, null, null, true, false);
        Visit nearestVisit = visits.get(0);
        for (Visit visit : visits) {
            if (nearestVisit.getStartDatetime().before(visit.getStartDatetime())) {
                nearestVisit = visit;
            }
        }
        return nearestVisit;
    }
}
