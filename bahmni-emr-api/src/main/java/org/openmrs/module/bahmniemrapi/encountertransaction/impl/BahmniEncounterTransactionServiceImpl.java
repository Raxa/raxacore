package org.openmrs.module.bahmniemrapi.encountertransaction.impl;


import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.SaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.LocationBasedEncounterTypeIdentifier;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class BahmniEncounterTransactionServiceImpl implements BahmniEncounterTransactionService {
//<<<<<<< HEAD

    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private EncounterTransactionMapper encounterTransactionMapper;
//    private ObsService obsService;
//    private AccessionNotesMapper accessionNotesMapper;
//    private EncounterTransactionObsMapper encounterTransactionObsMapper;
    private LocationBasedEncounterTypeIdentifier locationBasedEncounterTypeIdentifier;
    private List<SaveCommand> saveCommands;
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;

/*    public BahmniEncounterTransactionServiceImpl(ConceptService conceptService,
                                                 EncounterService encounterService, ObsService obsService,
                                                 EmrEncounterService emrEncounterService, EncounterTransactionMapper encounterTransactionMapper,
                                                 EncounterTransactionObsMapper encounterTransactionObsMapper, AccessionNotesMapper accessionNotesMapper, LocationBasedEncounterTypeIdentifier locationBasedEncounterTypeIdentifier) {
        this.conceptService = conceptService;
        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.obsService = obsService;
        this.accessionNotesMapper = accessionNotesMapper;
        this.encounterTransactionObsMapper = encounterTransactionObsMapper;
        this.locationBasedEncounterTypeIdentifier = locationBasedEncounterTypeIdentifier;
=======*/


    @Autowired
    public BahmniEncounterTransactionServiceImpl(EncounterService encounterService, EmrEncounterService emrEncounterService, EncounterTransactionMapper encounterTransactionMapper, LocationBasedEncounterTypeIdentifier locationBasedEncounterTypeIdentifier, List<SaveCommand> saveCommands, BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper) {
        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.locationBasedEncounterTypeIdentifier = locationBasedEncounterTypeIdentifier;
        this.saveCommands = saveCommands;
        this.bahmniEncounterTransactionMapper = bahmniEncounterTransactionMapper;
//        this.locationBasedEncounterTypeIdentifier = locationBasedEncounterTypeIdentifier;

//            >>>>>>> obsrelationship
    }

    @Override
    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction) {
        // TODO : Mujir - map string VisitType to the uuids and set on bahmniEncounterTransaction object
/*<<<<<<< HEAD
        String encounterTypeString = bahmniEncounterTransaction.getEncounterType();
        locationBasedEncounterTypeIdentifier.populateEncounterType(bahmniEncounterTransaction);

        if (bahmniEncounterTransaction.getEncounterTypeUuid() == null && StringUtils.isNotEmpty(encounterTypeString)) {
            EncounterType encounterType = encounterService.getEncounterType(encounterTypeString);
            if (encounterType == null) {
                throw new APIException("Encounter type:'" + encounterTypeString + "' not found.");
            }
            bahmniEncounterTransaction.setEncounterTypeUuid(encounterType.getUuid());
        }

        new EncounterTransactionDiagnosisMapper().populateDiagnosis(bahmniEncounterTransaction);

        EncounterTransaction encounterTransaction = emrEncounterService.save(bahmniEncounterTransaction);
=======*/
        setEncounterType(bahmniEncounterTransaction);
        EncounterTransaction encounterTransaction = emrEncounterService.save(bahmniEncounterTransaction.toEncounterTransaction());
//>>>>>>> obsrelationship

        //Get the saved encounter transaction from emr-api
        String encounterUuid = encounterTransaction.getEncounterUuid();
        Encounter currentEncounter = encounterService.getEncounterByUuid(encounterUuid);

        EncounterTransaction updatedEncounterTransaction=encounterTransactionMapper.map(currentEncounter, true);
        for (SaveCommand saveCommand : saveCommands) {
            updatedEncounterTransaction = saveCommand.save(bahmniEncounterTransaction,currentEncounter, updatedEncounterTransaction);
        }
        return bahmniEncounterTransactionMapper.map(updatedEncounterTransaction);
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
