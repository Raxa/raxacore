package org.openmrs.module.bahmniemrapi.encountertransaction.impl;


import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPostSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.LocationBasedEncounterTypeIdentifier;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.RetrospectiveEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.VisitIdentificationHelper;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public class BahmniEncounterTransactionServiceImpl implements BahmniEncounterTransactionService {
    public static final String VISIT_STATUS_ATTRIBUTE_TYPE = "Visit Status";
    public static final String EMERGENCY_VISIT_TYPE = "Emergency";
    public static final String OPD_VISIT_TYPE = "OPD";
    public static final String ADMISSION_ENCOUNTER_TYPE = "ADMISSION";
    public static final String IPD_VISIT_TYPE = "IPD";
    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private LocationBasedEncounterTypeIdentifier locationBasedEncounterTypeIdentifier;
    private EncounterDataPreSaveCommand encounterDataPreSaveCommand;
    private List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands;
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;
    private VisitService visitService;
    private PatientService patientService;

    public BahmniEncounterTransactionServiceImpl(EncounterService encounterService, EmrEncounterService emrEncounterService, EncounterTransactionMapper encounterTransactionMapper,
                                                 LocationBasedEncounterTypeIdentifier locationBasedEncounterTypeIdentifier, EncounterDataPreSaveCommand encounterDataPreSaveCommand, List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands,
                                                 BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper, VisitService visitService, PatientService patientService) {

        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.locationBasedEncounterTypeIdentifier = locationBasedEncounterTypeIdentifier;
        this.encounterDataPreSaveCommand = encounterDataPreSaveCommand;
        this.encounterDataPostSaveCommands = encounterDataPostSaveCommands;
        this.bahmniEncounterTransactionMapper = bahmniEncounterTransactionMapper;
        this.visitService = visitService;
        this.patientService = patientService;
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {
        // TODO : Mujir - map string VisitType to the uuids and set on bahmniEncounterTransaction object
        setEncounterType(bahmniEncounterTransaction);
        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
        VisitIdentificationHelper visitIdentificationHelper = new VisitIdentificationHelper(visitService);
        bahmniEncounterTransaction = new RetrospectiveEncounterTransactionService(visitIdentificationHelper).updatePastEncounters(bahmniEncounterTransaction, patient, visitStartDate, visitEndDate);

        EncounterTransaction encounterTransaction = emrEncounterService.save(bahmniEncounterTransaction.toEncounterTransaction());
        //Get the saved encounter transaction from emr-api
        String encounterUuid = encounterTransaction.getEncounterUuid();
        Encounter currentEncounter = encounterService.getEncounterByUuid(encounterUuid);

        Visit updatedVisit = createOrUpdateVisitAttribute(currentEncounter);
        currentEncounter.setVisit(updatedVisit);
        boolean includeAll = false;
        EncounterTransaction updatedEncounterTransaction = encounterTransactionMapper.map(currentEncounter, includeAll);
        for (EncounterDataPostSaveCommand saveCommand : encounterDataPostSaveCommands) {
            updatedEncounterTransaction = saveCommand.save(bahmniEncounterTransaction,currentEncounter, updatedEncounterTransaction);
        }
        return bahmniEncounterTransactionMapper.map(updatedEncounterTransaction, includeAll);
    }

    private Visit createOrUpdateVisitAttribute(Encounter currentEncounter) {
        Visit visit = currentEncounter.getVisit();
        VisitAttribute visitStatus = findVisitAttribute(visit, VISIT_STATUS_ATTRIBUTE_TYPE);

        if (visitStatus == null) {
            String value;
            if (visit.getVisitType().getName().equalsIgnoreCase(EMERGENCY_VISIT_TYPE)) {
                value = visit.getVisitType().getName();
            } else {
                value = OPD_VISIT_TYPE;
            }
            visitStatus = createVisitAttribute(visit, value, VISIT_STATUS_ATTRIBUTE_TYPE);
        }
        if (currentEncounter.getEncounterType().getName().equalsIgnoreCase(ADMISSION_ENCOUNTER_TYPE)) {
            visitStatus.setValueReferenceInternal(IPD_VISIT_TYPE);
        }
        visit.setAttribute(visitStatus);
        return visitService.saveVisit(visit);
    }

    private VisitAttribute createVisitAttribute(Visit visit, String value, String visitAttributeTypeName) {
        VisitAttribute visitStatus = new VisitAttribute();
        visitStatus.setVisit(visit);
        visitStatus.setAttributeType(getVisitAttributeType(visitAttributeTypeName));
        visitStatus.setValueReferenceInternal(value);
        return visitStatus;
    }

    private VisitAttributeType getVisitAttributeType(String visitAttributeTypeName) {
        for (VisitAttributeType visitAttributeType : visitService.getAllVisitAttributeTypes()) {
            if (visitAttributeType.getName().equalsIgnoreCase(visitAttributeTypeName)) {
                return visitAttributeType;
            }
        }
        return null;
    }

    private VisitAttribute findVisitAttribute(Visit visit, String visitAttributeTypeName) {
        for (VisitAttribute visitAttribute : visit.getAttributes()) {
            if (visitAttribute.getAttributeType().getName().equalsIgnoreCase(visitAttributeTypeName)) {
                return visitAttribute;
            }
        }
        return null;
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Patient patientByUuid = patientService.getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());
        return save(bahmniEncounterTransaction, patientByUuid, null, null);
    }

    private void setEncounterType(BahmniEncounterTransaction bahmniEncounterTransaction) {
        String encounterTypeString = bahmniEncounterTransaction.getEncounterType();
        locationBasedEncounterTypeIdentifier.populateEncounterType(bahmniEncounterTransaction);
        if (bahmniEncounterTransaction.getEncounterTypeUuid() == null && StringUtils.isNotEmpty(encounterTypeString)) {
            EncounterType encounterType = encounterService.getEncounterType(encounterTypeString);
            if (encounterType == null) {
                throw new RuntimeException("Encounter type:'" + encounterTypeString + "' not found.");
            }
            bahmniEncounterTransaction.setEncounterTypeUuid(encounterType.getUuid());
        }
    }


}
