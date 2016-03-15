package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bedmanagement.BedManagementService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/discharge")
public class BahmniDischargeController {

    private BahmniEncounterTransactionService bahmniEncounterTransactionService;
    private BedManagementService bedManagementService;
    private PatientService patientService;

    @Autowired
    public BahmniDischargeController(BahmniEncounterTransactionService bahmniEncounterTransactionService, BedManagementService bedManagementService, PatientService patientService) {
        this.bahmniEncounterTransactionService = bahmniEncounterTransactionService;
        this.bedManagementService = bedManagementService;
        this.patientService = patientService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public BahmniEncounterTransaction discharge(@RequestBody BahmniEncounterTransaction bahmniEncounterTransaction) {
        Patient patientByUuid = patientService.getPatientByUuid(bahmniEncounterTransaction.getPatientUuid());

        bedManagementService.unAssignPatientFromBed(patientByUuid);
        return bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
    }
}
