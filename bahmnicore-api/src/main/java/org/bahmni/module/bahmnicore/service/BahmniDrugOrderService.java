package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.drugorder.*;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.openmrs.*;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;

import java.text.ParseException;
import java.util.*;

public interface BahmniDrugOrderService {
    void add(String patientId, Date orderDate, List<BahmniFeedDrugOrder> bahmniDrugOrders, String systemUserName, String visitTypeName);
    List<DrugOrder> getActiveDrugOrders(String patientUuid, Date startDate, Date endDate);

    List<DrugOrder> getActiveDrugOrders(String patientUuid);

    List<DrugOrder> getPrescribedDrugOrders(List<String> visitUuids, String patientUuid, Boolean includeActiveVisit,
                                            Integer numberOfVisit, Date startDate, Date endDate, Boolean getEffectiveOrdersOnly);

    List<DrugOrder> getPrescribedDrugOrdersForConcepts(Patient patient, Boolean includeActiveVisit, List<Visit> visits, List<Concept> concepts, Date startDate, Date endDate);

    DrugOrderConfigResponse getConfig();

    List<Order> getAllDrugOrders(String patientUuid, Set<Concept> conceptsForDrugs, Date startDate, Date endDate,
                                 Set<Concept> drugConceptsToBeExcluded, Collection<Encounter> encounters) throws ParseException;

    Map<String,DrugOrder> getDiscontinuedDrugOrders(List<DrugOrder> drugOrders);

    List<DrugOrder> getInactiveDrugOrders(String patientUuid, Set<Concept> concepts, Set<Concept> drugConceptsToBeExcluded,
                                          Collection<Encounter> encountersByPatientProgramUuid);

    List<BahmniDrugOrder> getDrugOrders(String patientUuid, Boolean isActive, Set<Concept> conceptsToFilter, Set<Concept> conceptsToExclude,
                                        String patientProgramUuid) throws ParseException;
}
