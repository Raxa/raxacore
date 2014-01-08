package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BahmniDrugOrderServiceImpl implements BahmniDrugOrderService {

    private VisitService visitService;
    private PatientService patientService;
    private ConceptService conceptService;
    private OrderService orderService;
    private EncounterService encounterService;
    private ProviderService providerService;
    private OrderType drugOrderType = null;
    private Provider systemProvider = null;
    private EncounterRole unknownEncounterRole = null;
    private EncounterType consultationEncounterType = null;

    @Autowired
    public BahmniDrugOrderServiceImpl(VisitService visitService, PatientService patientService, ConceptService conceptService, OrderService orderService, ProviderService providerService, EncounterService encounterService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.conceptService = conceptService;
        this.orderService = orderService;
        this.providerService = providerService;
        this.encounterService = encounterService;
    }

    @Override
    public void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders) {
        Patient patient = patientService.getPatients(null, patientId, null, true, null, null).get(0);
        List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
        if (!activeVisits.isEmpty()){
            Visit activeVisit = activeVisits.get(0);
            Encounter encounter = new Encounter();
            encounter.setOrders(createOrders(patient, orderDate, encounter, bahmniDrugOrders));
            encounter.setProvider(getEncounterRole(), getSystemProvider());
            encounter.setEncounterType(getConsultationEncounterType());
            activeVisit.addEncounter(encounter);
            visitService.saveVisit(activeVisit);
        }
    }

    private EncounterType getConsultationEncounterType() {
        if(consultationEncounterType == null){
            consultationEncounterType = encounterService.getEncounterType("OPD");
        }
        return consultationEncounterType;
    }

    private EncounterRole getEncounterRole() {
        if(unknownEncounterRole == null) {
            for (EncounterRole encounterRole : encounterService.getAllEncounterRoles(false)) {
                if(encounterRole.getName().equalsIgnoreCase("unknown")) {
                    unknownEncounterRole = encounterRole;
                }
            }
        }
        return unknownEncounterRole;
    }

    private Provider getSystemProvider() {
        if(systemProvider == null){
            List<Provider> providers = providerService.getProviders("system", null, null, null, false);
            if(!providers.isEmpty()) {
                systemProvider = providers.get(0);
            }
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
        if(drugOrderType == null){
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
