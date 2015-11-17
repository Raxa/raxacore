package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.OrderMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.mapper.OrderMapper1_12;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Bridge between extension scripts of Bahmni and Bahmni core as well as OpenMRS core.
 */
@Component
@Scope("prototype")
public class BahmniBridge {

    private String patientUuid;
    private String visitUUid;

    private ObsDao obsDao;
    private PatientService patientService;
    private PersonService personService;
    private ConceptService conceptService;
    private OrderDao orderDao;
    private BahmniDrugOrderService bahmniDrugOrderService;

    OrderMapper drugOrderMapper = new OrderMapper1_12();
    /**
     * Factory method to construct objects of <code>BahmniBridge</code>.
     * <p/>
     * This is provided so that <code>BahmniBridge</code> can be called by extensions without having to use the
     * Spring application context. Prefer using this as opposed to the constructor.
     *
     * @return
     */
    public static BahmniBridge create() {
        return Context.getRegisteredComponents(BahmniBridge.class).iterator().next();
    }

    @Autowired
    public BahmniBridge(ObsDao obsDao, PatientService patientService, PersonService personService, ConceptService conceptService, OrderDao orderDao, BahmniDrugOrderService bahmniDrugOrderService) {
        this.obsDao = obsDao;
        this.patientService = patientService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.orderDao = orderDao;
        this.bahmniDrugOrderService = bahmniDrugOrderService;
    }

    /**
     * Set patient uuid. This will be used by methods that require the patient to perform its operations.
     * <p/>
     * Setting patient uuid might be mandatory depending on the operation you intend to perform using the bridge.
     *
     * @param patientUuid
     * @return
     */
    public BahmniBridge forPatient(String patientUuid) {
        this.patientUuid = patientUuid;
        return this;
    }

    /**
     * Set visit uuid. This will be used by methods that require a visit to perform its operations.
     * <p/>
     * Setting visit uuid might be mandatory depending on the operation you intend to perform using the bridge.
     *
     * @param visitUuid
     * @return
     */
    public BahmniBridge forVisit(String visitUuid) {
        this.visitUUid = visitUuid;
        return this;
    }

    /**
     * Retrieve last observation for <code>patientUuid</code> set using {@link org.bahmni.module.bahmnicore.service.impl.BahmniBridge#forPatient(String)}
     * for the given <code>conceptName</code>.
     *
     * @param conceptName
     * @return
     */
    public Obs latestObs(String conceptName) {
        List<Obs> obsList = obsDao.getLatestObsFor(patientUuid, conceptName, 1);
        if (obsList.size() > 0) {
            return obsList.get(0);
        }
        return null;
    }

    /**
     * Retrieve age in years for <code>patientUuid</code> set using {@link org.bahmni.module.bahmnicore.service.impl.BahmniBridge#forPatient(String)}
     *
     * @param asOfDate
     * @return
     */
    public Integer ageInYears(Date asOfDate) {
        Date birthdate = patientService.getPatientByUuid(patientUuid).getBirthdate();
        return Years.yearsBetween(new LocalDate(birthdate), new LocalDate(asOfDate)).getYears();

    }

    /**
     * Retrieve drug orders set for <code>regimenName</code>
     *
     * @param regimenName
     * @return
     */
    public Collection<EncounterTransaction.DrugOrder> drugOrdersForRegimen(String regimenName) {
        return orderDao.getDrugOrderForRegimen(regimenName);
    }

    /**
     * Retrieve active Drug orders for <code>patientUuid<code/>
     *
     * @return
     */
    public List<EncounterTransaction.DrugOrder> activeDrugOrdersForPatient() {
        List<DrugOrder> activeOpenMRSDrugOrders = bahmniDrugOrderService.getActiveDrugOrders(patientUuid);
        List<EncounterTransaction.DrugOrder> drugOrders = new ArrayList<>();
        for (DrugOrder activeOpenMRSDrugOrder : activeOpenMRSDrugOrders) {
            EncounterTransaction.DrugOrder drugOrder = drugOrderMapper.mapDrugOrder(activeOpenMRSDrugOrder);
            if ((isNotScheduled(drugOrder) || hasScheduledOrderBecameActive(drugOrder)) && isNotStopped(drugOrder)) {
                drugOrders.add(drugOrder);
            }
        }
        return drugOrders;
    }

    /**
     * Retrieve person attribute type for <code>attributeType</code>
     *
     * @return
     */
    public PersonAttributeType getPersonAttributeType(String attributeType) {
        return personService.getPersonAttributeTypeByName(attributeType);
    }

    /**
     * Retrieve concept for <code>conceptName</code>
     *
     * @return
     */
    public Concept getConcept(String conceptName) {
        return conceptService.getConceptByName(conceptName);
    }
    private boolean isNotScheduled(EncounterTransaction.DrugOrder drugOrder) {
        return drugOrder.getScheduledDate() == null;
    }

    private boolean isNotStopped(EncounterTransaction.DrugOrder drugOrder) {
        return drugOrder.getEffectiveStopDate() == null || drugOrder.getEffectiveStopDate().after(new Date());
    }

    private boolean hasScheduledOrderBecameActive(EncounterTransaction.DrugOrder drugOrder) {

        return drugOrder.getScheduledDate().before(new Date());
    }


}
