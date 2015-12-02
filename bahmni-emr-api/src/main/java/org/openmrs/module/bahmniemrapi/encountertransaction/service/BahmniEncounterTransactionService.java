package org.openmrs.module.bahmniemrapi.encountertransaction.service;

import org.openmrs.Patient;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.EncounterSearchParameters;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Date;

public interface BahmniEncounterTransactionService {
    BahmniEncounterTransaction save(BahmniEncounterTransaction encounterTransaction);

    BahmniEncounterTransaction save(BahmniEncounterTransaction encounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate);

    EncounterTransaction find(EncounterSearchParameters encounterSearchParameters);

    void delete(BahmniEncounterTransaction bahmniEncounterTransaction);
}
