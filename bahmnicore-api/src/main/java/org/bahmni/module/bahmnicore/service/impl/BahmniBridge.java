package org.bahmni.module.bahmnicore.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    private OrderDao orderDao;

    /**
     * Factory method to construct objects of <code>BahmniBridge</code>.
     *
     * This is provided so that <code>BahmniBridge</code> can be called by extensions without having to use the
     * Spring application context. Prefer using this as opposed to the constructor.
     *
     * @return
     */
    public static BahmniBridge create() {
        return Context.getRegisteredComponents(BahmniBridge.class).iterator().next();
    }

    @Autowired
    public BahmniBridge(ObsDao obsDao, PatientService patientService, OrderDao orderDao) {
        this.obsDao = obsDao;
        this.patientService = patientService;
        this.orderDao = orderDao;
    }

    /**
     * Set patient uuid. This will be used by methods that require the patient to perform its operations.
     *
     * Setting patient uuid might be mandatory depending on the operation you intend to perform using the bridge.
     * @param patientUuid
     * @return
     */
    public BahmniBridge forPatient(String patientUuid) {
        this.patientUuid = patientUuid;
        return this;
    }

    /**
     * Set visit uuid. This will be used by methods that require a visit to perform its operations.
     *
     * Setting visit uuid might be mandatory depending on the operation you intend to perform using the bridge.
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
}
