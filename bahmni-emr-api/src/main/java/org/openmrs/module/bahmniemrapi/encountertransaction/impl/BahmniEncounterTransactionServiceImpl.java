package org.openmrs.module.bahmniemrapi.encountertransaction.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPostSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.RetrospectiveEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.VisitIdentificationHelper;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterSearchParameters;
import org.openmrs.module.emrapi.encounter.EncounterSearchParametersBuilder;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
public class BahmniEncounterTransactionServiceImpl implements BahmniEncounterTransactionService {
    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private EncounterTypeIdentifier encounterTypeIdentifier;
    private EncounterDataPreSaveCommand encounterDataPreSaveCommand;
    private List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands;
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;
    private VisitService visitService;
    private PatientService patientService;
    private LocationService locationService;
    private ProviderService providerService;

    public BahmniEncounterTransactionServiceImpl(EncounterService encounterService, EmrEncounterService emrEncounterService, EncounterTransactionMapper encounterTransactionMapper,
                                                 EncounterTypeIdentifier encounterTypeIdentifier, EncounterDataPreSaveCommand encounterDataPreSaveCommand, List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands,
                                                 BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper, VisitService visitService, PatientService patientService, LocationService locationService, ProviderService providerService) {

        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.encounterTypeIdentifier = encounterTypeIdentifier;
        this.encounterDataPreSaveCommand = encounterDataPreSaveCommand;
        this.encounterDataPostSaveCommands = encounterDataPostSaveCommands;
        this.bahmniEncounterTransactionMapper = bahmniEncounterTransactionMapper;
        this.visitService = visitService;
        this.patientService = patientService;
        this.locationService = locationService;
        this.providerService = providerService;
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {
        // TODO : Mujir - map string VisitType to the uuids and set on bahmniEncounterTransaction object
        if (StringUtils.isBlank(bahmniEncounterTransaction.getEncounterTypeUuid())) {
            setEncounterType(bahmniEncounterTransaction);
        }
        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
        VisitIdentificationHelper visitIdentificationHelper = new VisitIdentificationHelper(visitService);
        bahmniEncounterTransaction = new RetrospectiveEncounterTransactionService(visitIdentificationHelper).updatePastEncounters(bahmniEncounterTransaction, patient, visitStartDate, visitEndDate);

        EncounterTransaction encounterTransaction = emrEncounterService.save(bahmniEncounterTransaction.toEncounterTransaction());
        //Get the saved encounter transaction from emr-api
        String encounterUuid = encounterTransaction.getEncounterUuid();
        Encounter currentEncounter = encounterService.getEncounterByUuid(encounterUuid);

        boolean includeAll = false;
        EncounterTransaction updatedEncounterTransaction = encounterTransactionMapper.map(currentEncounter, includeAll);
        for (EncounterDataPostSaveCommand saveCommand : encounterDataPostSaveCommands) {
            updatedEncounterTransaction = saveCommand.save(bahmniEncounterTransaction,currentEncounter, updatedEncounterTransaction);
        }
        return bahmniEncounterTransactionMapper.map(updatedEncounterTransaction, includeAll);
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Patient patientByUuid = patientService.getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());
        return save(bahmniEncounterTransaction, patientByUuid, null, null);
    }

    @Override
    public List<EncounterTransaction> find(EncounterSearchParameters encounterSearchParameters) {
        User loginUser = Context.getUserContext().getAuthenticatedUser();
        List<Encounter> loggedInUserEncounters = new ArrayList<>();
        EncounterSearchParametersBuilder searchParameters = new EncounterSearchParametersBuilder(encounterSearchParameters,
                this.patientService, this.encounterService, this.locationService, this.providerService, this.visitService);
        List<Encounter> encounters = this.encounterService.getEncounters(searchParameters.getPatient(), searchParameters.getLocation(), searchParameters.getStartDate(), searchParameters.getEndDate(), new ArrayList(), searchParameters.getEncounterTypes(), searchParameters.getProviders(), searchParameters.getVisitTypes(), searchParameters.getVisits(), searchParameters.getIncludeAll().booleanValue());
        if (CollectionUtils.isNotEmpty(encounters)) {
            for (Encounter encounter : encounters) {
                if (encounter.getCreator().getId().equals(loginUser.getId())) {
                    loggedInUserEncounters.add(encounter);
                }
            }
        }
        return this.getEncounterTransactions(loggedInUserEncounters, encounterSearchParameters.getIncludeAll().booleanValue());
    }

    private void setEncounterType(BahmniEncounterTransaction bahmniEncounterTransaction) {
        EncounterType encounterType = encounterTypeIdentifier.getEncounterType(bahmniEncounterTransaction);
        if (encounterType == null) {
            throw new RuntimeException("Encounter type not found.");
        }
        bahmniEncounterTransaction.setEncounterTypeUuid(encounterType.getUuid());
    }

    private List<EncounterTransaction> getEncounterTransactions(List<Encounter> encounters, boolean includeAll) {
        List<EncounterTransaction> encounterTransactions = new ArrayList<>();
        for (Encounter encounter : encounters) {
            encounterTransactions.add(this.encounterTransactionMapper.map(encounter, Boolean.valueOf(includeAll)));
        }
        return encounterTransactions;
    }

}
