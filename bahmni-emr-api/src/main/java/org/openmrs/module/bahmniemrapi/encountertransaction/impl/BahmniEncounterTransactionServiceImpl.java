package org.openmrs.module.bahmniemrapi.encountertransaction.impl;


import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bahmniemrapi.BahmniEmrAPIException;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPostSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterSearchParameters;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.RetrospectiveEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.VisitIdentificationHelper;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.VisitMatcher;
import org.openmrs.module.bahmniemrapi.visitlocation.BahmniVisitLocationService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.EncounterSearchParametersBuilder;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class BahmniEncounterTransactionServiceImpl extends BaseOpenmrsService implements BahmniEncounterTransactionService {
    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private EncounterTypeIdentifier encounterTypeIdentifier;
    private List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands;
    private List<EncounterDataPostSaveCommand> encounterDataPostDeleteCommands;
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;
    private VisitService visitService;
    private PatientService patientService;
    private LocationService locationService;
    private ProviderService providerService;
    private BaseEncounterMatcher encounterSessionMatcher;
    private BahmniVisitLocationService bahmniVisitLocationService;
    private Map<String, VisitMatcher> visitMatchersMap = new HashMap<>();

    public BahmniEncounterTransactionServiceImpl(EncounterService encounterService,
                                                 EmrEncounterService emrEncounterService,
                                                 EncounterTransactionMapper encounterTransactionMapper,
                                                 EncounterTypeIdentifier encounterTypeIdentifier,
                                                 List<EncounterDataPostSaveCommand> encounterDataPostSaveCommands,
                                                 List<EncounterDataPostSaveCommand> encounterDataPostDeleteCommands,
                                                 BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper,
                                                 VisitService visitService,
                                                 PatientService patientService,
                                                 LocationService locationService,
                                                 ProviderService providerService,
                                                 BaseEncounterMatcher encounterSessionMatcher,
                                                 BahmniVisitLocationService bahmniVisitLocationService) {

        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.encounterTypeIdentifier = encounterTypeIdentifier;
        this.encounterDataPostSaveCommands = encounterDataPostSaveCommands;
        this.encounterDataPostDeleteCommands = encounterDataPostDeleteCommands;
        this.bahmniEncounterTransactionMapper = bahmniEncounterTransactionMapper;
        this.visitService = visitService;
        this.patientService = patientService;
        this.locationService = locationService;
        this.providerService = providerService;
        this.encounterSessionMatcher = encounterSessionMatcher;
        this.bahmniVisitLocationService = bahmniVisitLocationService;
    }

    @Override
    public void onStartup() {
        super.onStartup();
        List<VisitMatcher> visitMatchers = Context.getRegisteredComponents(VisitMatcher.class);
        for (VisitMatcher visitMatcher : visitMatchers) {
            visitMatchersMap.put(visitMatcher.getClass().getCanonicalName(), visitMatcher);
        }
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {

        if(bahmniEncounterTransaction.getEncounterDateTime() == null){
            bahmniEncounterTransaction.setEncounterDateTime(new Date());
        }

        handleDrugOrders(bahmniEncounterTransaction,patient);

        if(!StringUtils.isBlank(bahmniEncounterTransaction.getEncounterUuid())){
            Encounter encounterByUuid = encounterService.getEncounterByUuid(bahmniEncounterTransaction.getEncounterUuid());
            if(encounterByUuid != null){
                bahmniEncounterTransaction.setEncounterTypeUuid(encounterByUuid.getEncounterType().getUuid());
            }
        }

        setVisitType(bahmniEncounterTransaction);

        if (StringUtils.isBlank(bahmniEncounterTransaction.getEncounterTypeUuid())) {
            setEncounterType(bahmniEncounterTransaction);
        }

        List<EncounterDataPreSaveCommand> encounterDataPreSaveCommands = Context.getRegisteredComponents(EncounterDataPreSaveCommand.class);
        for (EncounterDataPreSaveCommand saveCommand : encounterDataPreSaveCommands) {
            saveCommand.update(bahmniEncounterTransaction);
        }
        VisitMatcher visitMatcher = getVisitMatcher();
        if (BahmniEncounterTransaction.isRetrospectiveEntry(bahmniEncounterTransaction.getEncounterDateTime())) {
            bahmniEncounterTransaction = new RetrospectiveEncounterTransactionService(visitMatcher).updatePastEncounters(bahmniEncounterTransaction, patient, visitStartDate, visitEndDate);
        }

        if (!StringUtils.isBlank(bahmniEncounterTransaction.getVisitType())) {
            setVisitTypeUuid(visitMatcher, bahmniEncounterTransaction);
        }

        setVisitLocationToEncounterTransaction(bahmniEncounterTransaction);

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

    private EncounterTransaction setVisitLocationToEncounterTransaction(BahmniEncounterTransaction bahmniEncounterTransaction) {
        if(bahmniEncounterTransaction.toEncounterTransaction().getLocationUuid() != null) {
            String visitLocationUuid = bahmniVisitLocationService.getVisitLocationUuid(bahmniEncounterTransaction.toEncounterTransaction().getLocationUuid());
            bahmniEncounterTransaction.toEncounterTransaction().setVisitLocationUuid(visitLocationUuid);
        }
        return bahmniEncounterTransaction.toEncounterTransaction();
    }

    private VisitMatcher getVisitMatcher() {
        String globalProperty = Context.getAdministrationService().getGlobalProperty("bahmni.visitMatcher");
        if(visitMatchersMap.get(globalProperty)!=null) {
            return visitMatchersMap.get(globalProperty);
        }
        return new VisitIdentificationHelper(visitService, bahmniVisitLocationService);
    }

    private void handleDrugOrders(BahmniEncounterTransaction bahmniEncounterTransaction,Patient patient) {

        bahmniEncounterTransaction.updateDrugOrderIfScheduledDateNotSet(new Date());

        if(bahmniEncounterTransaction.hasPastDrugOrders()){
            BahmniEncounterTransaction pastEncounterTransaction = bahmniEncounterTransaction.cloneForPastDrugOrders();
            save(pastEncounterTransaction,patient,null,null);
            bahmniEncounterTransaction.clearDrugOrders();
        }
    }

    private void setVisitType(BahmniEncounterTransaction bahmniEncounterTransaction) {
        if(!StringUtils.isBlank(bahmniEncounterTransaction.getVisitTypeUuid())){
            bahmniEncounterTransaction.setVisitType(getVisitTypeByUuid(bahmniEncounterTransaction.getVisitTypeUuid()).getName());
        }
    }

    private VisitType getVisitTypeByUuid(String uuid){
        VisitType visitType = visitService.getVisitTypeByUuid(uuid);
        if(visitType == null){
            throw new BahmniEmrAPIException("Cannot find visit type with UUID "+ visitType);
        }
        return visitType;
    }

    private void setVisitTypeUuid(VisitMatcher visitMatcher, BahmniEncounterTransaction bahmniEncounterTransaction) {
        VisitType visitType = visitMatcher.getVisitTypeByName(bahmniEncounterTransaction.getVisitType());
        if (visitType != null) {
            bahmniEncounterTransaction.setVisitTypeUuid(visitType.getUuid());
        }
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Patient patientByUuid = patientService.getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());
        return save(bahmniEncounterTransaction, patientByUuid, null, null);
    }

    @Override
    public EncounterTransaction find(BahmniEncounterSearchParameters encounterSearchParameters) {
        EncounterSearchParametersBuilder searchParametersBuilder = new EncounterSearchParametersBuilder(encounterSearchParameters,
                this.patientService, this.encounterService, this.locationService, this.providerService, this.visitService);

        Visit visit = null;
        if(!BahmniEncounterTransaction.isRetrospectiveEntry(searchParametersBuilder.getEndDate())){
            List<Visit> visits = this.visitService.getActiveVisitsByPatient(searchParametersBuilder.getPatient());
            visit = bahmniVisitLocationService.getMatchingVisitInLocation(visits, encounterSearchParameters.getLocationUuid());
            if (visit == null) return null;
        }
        Encounter encounter = encounterSessionMatcher.findEncounter(visit, mapEncounterParameters(searchParametersBuilder, encounterSearchParameters));
        return encounter != null ? encounterTransactionMapper.map(encounter, encounterSearchParameters.getIncludeAll()) : null;
    }

    private EncounterParameters mapEncounterParameters(EncounterSearchParametersBuilder encounterSearchParameters, BahmniEncounterSearchParameters searchParameters) {
        EncounterParameters encounterParameters = EncounterParameters.instance();
        encounterParameters.setPatient(encounterSearchParameters.getPatient());
        if(encounterSearchParameters.getEncounterTypes().size() > 0){
            encounterParameters.setEncounterType(encounterSearchParameters.getEncounterTypes().iterator().next());
        }
        encounterParameters.setProviders(new HashSet<>(encounterSearchParameters.getProviders()));
        HashMap<String, Object> context = new HashMap<>();
        context.put("patientProgramUuid", searchParameters.getPatientProgramUuid());
        encounterParameters.setContext(context);
        encounterParameters.setEncounterDateTime(encounterSearchParameters.getEndDate());
        encounterParameters.setLocation(encounterSearchParameters.getLocation());
        return encounterParameters;
    }

    @Override
    public void delete(BahmniEncounterTransaction bahmniEncounterTransaction) {
        Encounter encounter = encounterService.getEncounterByUuid(bahmniEncounterTransaction.getEncounterUuid());
        encounterService.voidEncounter(encounter, bahmniEncounterTransaction.getReason());
        for (EncounterDataPostSaveCommand saveCommand : encounterDataPostDeleteCommands) {
            saveCommand.save(bahmniEncounterTransaction,encounter, null);
        }
    }

    private void setEncounterType(BahmniEncounterTransaction bahmniEncounterTransaction) {
        EncounterType encounterType = encounterTypeIdentifier.getEncounterTypeFor(bahmniEncounterTransaction.getEncounterType(), bahmniEncounterTransaction.getLocationUuid());
        if (encounterType == null) {
            throw new RuntimeException("Encounter type not found.");
        }
        bahmniEncounterTransaction.setEncounterTypeUuid(encounterType.getUuid());
    }

}
