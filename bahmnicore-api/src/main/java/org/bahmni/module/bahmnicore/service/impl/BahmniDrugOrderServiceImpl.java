package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.VisitIdentifierService;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BahmniDrugOrderServiceImpl implements BahmniDrugOrderService {

    public static final String DRUG_ORDER = "DRUG_ORDER";

    private VisitService visitService;
    private PatientService patientService;
    private ConceptService conceptService;
    private OrderService orderService;
    private EncounterService encounterService;
    private ProviderService providerService;
    private UserService userService;
    private OrderType drugOrderType = null;
    private Provider systemProvider = null;
    private EncounterRole unknownEncounterRole = null;
    private EncounterType consultationEncounterType = null;
    private String systemUserName = null;

    @Autowired
    public BahmniDrugOrderServiceImpl(VisitService visitService, PatientService patientService,
                                      ConceptService conceptService, OrderService orderService,
                                      ProviderService providerService, EncounterService encounterService,
                                      UserService userService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.conceptService = conceptService;
        this.orderService = orderService;
        this.providerService = providerService;
        this.encounterService = encounterService;
        this.userService = userService;
    }

    @Override
    public void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders, String systemUserName) {
        this.systemUserName = systemUserName;
        Patient patient = patientService.getPatients(null, patientId, null, true, null, null).get(0);


//        List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
//        if (!activeVisits.isEmpty()) {
//            addDrugOrdersToVisit(orderDate, bahmniDrugOrders, patient, activeVisits.get(0));
//        } else {
//            List<Visit> visits = visitService.getVisits(null, Arrays.asList(patient), null, null, null, orderDate, null, null, null, true, false);
//            addDrugOrdersToVisit(orderDate, bahmniDrugOrders, patient, visits.get(0));
//        }

        // TODO : Mujir/Vinay/Angshu - visit type should NEVER be used in code. "DRUG ORDER" is used below.. need to change
        Visit visit = new VisitIdentifierService(visitService).findOrInitializeVisit(patient, orderDate, DRUG_ORDER);
        addDrugOrdersToVisit(orderDate, bahmniDrugOrders, patient, visit);
    }

    private void addDrugOrdersToVisit(Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders, Patient patient, Visit visit) {
        Set<Encounter> encounters = visit.getEncounters();
        Encounter systemConsultationEncounter = null;

        if (encounters != null && encounters.size() > 0)
            systemConsultationEncounter = getSystemConsultationEncounter(encounters);

        if (systemConsultationEncounter == null) {
            systemConsultationEncounter = createNewSystemConsultationEncounter(orderDate, patient);
        }

        Set<Order> drugOrders = createOrders(patient, orderDate, systemConsultationEncounter, bahmniDrugOrders);
        for (Order drugOrder : drugOrders) {
            systemConsultationEncounter.addOrder(drugOrder);
        }
        visit.addEncounter(systemConsultationEncounter);
        Date visitStopDatetime = visit.getStopDatetime();
        if (visitStopDatetime != null && visitStopDatetime.compareTo(orderDate) < 0) {
            visit.setStopDatetime(orderDate);
        }
        visitService.saveVisit(visit);
    }

    private Encounter createNewSystemConsultationEncounter(Date orderDate, Patient patient) {
        Encounter systemConsultationEncounter;
        systemConsultationEncounter = new Encounter();
        systemConsultationEncounter.setProvider(getEncounterRole(), getSystemProvider());
        systemConsultationEncounter.setEncounterType(getConsultationEncounterType());
        systemConsultationEncounter.setPatient(patient);
        systemConsultationEncounter.setEncounterDatetime(orderDate);
        return systemConsultationEncounter;
    }

    private Encounter getSystemConsultationEncounter(Set<Encounter> encounters) {
        for (Encounter encounter : encounters) {
            if (isSystemConsultationEncounter(encounter)) {
                return encounter;
            }
        }
        return null;
    }

    private boolean isSystemConsultationEncounter(Encounter encounter) {
        boolean isSystemEncounter = false;
        Provider systemProvider = getSystemProvider();
        Set<EncounterProvider> encounterProviders = encounter.getEncounterProviders();
        for (EncounterProvider encounterProvider : encounterProviders) {
            if (encounterProvider.getProvider().getId() == systemProvider.getId())
                isSystemEncounter = true;
        }
        return encounter.getEncounterType().equals(getConsultationEncounterType()) && isSystemEncounter;
    }

    private EncounterType getConsultationEncounterType() {
        if (consultationEncounterType == null) {
            consultationEncounterType = encounterService.getEncounterType("OPD");
        }
        return consultationEncounterType;
    }

    private EncounterRole getEncounterRole() {
        if (unknownEncounterRole == null) {
            for (EncounterRole encounterRole : encounterService.getAllEncounterRoles(false)) {
                if (encounterRole.getName().equalsIgnoreCase("unknown")) {
                    unknownEncounterRole = encounterRole;
                }
            }
        }
        return unknownEncounterRole;
    }

    private Provider getSystemProvider() {
        if (systemProvider == null) {
            User systemUser = userService.getUserByUsername(systemUserName);
            Collection<Provider> providers = providerService.getProvidersByPerson(systemUser.getPerson());
            systemProvider = providers == null ? null : providers.iterator().next();
        }
        return systemProvider;
    }

    private Set<Order> createOrders(Patient patient, Date orderDate, Encounter encounter, List<BahmniDrugOrder> bahmniDrugOrders) {
        Set<Order> orders = new HashSet<>();
        for (BahmniDrugOrder bahmniDrugOrder : bahmniDrugOrders) {
            DrugOrder drugOrder = new DrugOrder();
            Drug drug = conceptService.getDrugByUuid(bahmniDrugOrder.getProductUuid());
            drugOrder.setDrug(drug);
            drugOrder.setConcept(drug.getConcept());
            drugOrder.setDose(bahmniDrugOrder.getDosage());
            drugOrder.setStartDate(orderDate);
            drugOrder.setAutoExpireDate(DateUtils.addDays(orderDate, bahmniDrugOrder.getNumberOfDays()));
            drugOrder.setEncounter(encounter);
            drugOrder.setPatient(patient);
            drugOrder.setPrn(false);
            drugOrder.setOrderType(getDrugOrderType());
            orders.add(drugOrder);
        }
        return orders;
    }

    private OrderType getDrugOrderType() {
        if (drugOrderType == null) {
            List<OrderType> allOrderTypes = orderService.getAllOrderTypes();
            for (OrderType type : allOrderTypes) {
                if (type.getName().toLowerCase().equals("drug order")) {
                    drugOrderType = type;
                }
            }
        }
        return drugOrderType;
    }
}
