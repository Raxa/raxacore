package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.dao.BahmniPatientDao;
import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.util.VisitIdentificationHelper;
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
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BahmniDrugOrderServiceImpl implements BahmniDrugOrderService {
    private VisitService visitService;
    private ConceptService conceptService;
    private OrderService orderService;
    private EncounterService encounterService;
    private ProviderService providerService;
    private UserService userService;
    private BahmniPatientDao bahmniPatientDao;
    private PatientService openmrsPatientService;
    private OrderDao orderDao;
    private OrderType drugOrderType = null;
    private Provider systemProvider = null;
    private EncounterRole unknownEncounterRole = null;
    private EncounterType consultationEncounterType = null;
    private String systemUserName = null;
    private VisitType pharmacyVisitType = null;
    public static final String PHARMACY_VISIT = "PHARMACY VISIT";

    @Autowired
    public BahmniDrugOrderServiceImpl(VisitService visitService, ConceptService conceptService, OrderService orderService,
                                      ProviderService providerService, EncounterService encounterService,
                                      UserService userService, BahmniPatientDao bahmniPatientDao,
                                      PatientService patientService, OrderDao orderDao) {
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.orderService = orderService;
        this.providerService = providerService;
        this.encounterService = encounterService;
        this.userService = userService;
        this.bahmniPatientDao = bahmniPatientDao;
        this.openmrsPatientService = patientService;
        this.orderDao = orderDao;
    }

    @Override
    public void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders, String systemUserName) {
        this.systemUserName = systemUserName;
        Patient patient = bahmniPatientDao.getPatient(patientId);
        Visit visitForDrugOrders = new VisitIdentificationHelper(visitService).getVisitFor(patient, orderDate, PHARMACY_VISIT);
        addDrugOrdersToVisit(orderDate, bahmniDrugOrders, patient, visitForDrugOrders);
    }

    @Override
    public List<DrugOrder> getActiveDrugOrders(String patientUuid) {
        Patient patient = openmrsPatientService.getPatientByUuid(patientUuid);
        return orderDao.getActiveDrugOrders(patient);
    }

    @Override
    public List<DrugOrder> getPrescribedDrugOrders(String patientUuid, Boolean includeActiveVisit, Integer numberOfVisits) {
        Patient patient = openmrsPatientService.getPatientByUuid(patientUuid);
        return orderDao.getPrescribedDrugOrders(patient, includeActiveVisit, numberOfVisits);
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
            List<OrderType> allOrderTypes = orderService.getOrderTypes(true);
            for (OrderType type : allOrderTypes) {
                if (type.getName().toLowerCase().equals("drug order")) {
                    drugOrderType = type;
                }
            }
        }
        return drugOrderType;
    }
}
