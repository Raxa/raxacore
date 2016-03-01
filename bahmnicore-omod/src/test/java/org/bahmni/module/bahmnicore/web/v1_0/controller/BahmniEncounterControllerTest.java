package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterSearchParameters;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterSearchParameters;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BahmniEncounterControllerTest {
    @Mock
    private EmrEncounterService emrEncounterService;
    @Mock
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;
    @Mock
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;

    private BahmniEncounterController bahmniEncounterController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returns_multiple_encounterTransactions_if_exists() throws Exception {
        EncounterTransaction et1 = new EncounterTransaction();
        et1.setEncounterUuid("et1");

        BahmniEncounterSearchParameters encounterSearchParameters = new BahmniEncounterSearchParameters();
        encounterSearchParameters.setIncludeAll(false);

        when(bahmniEncounterTransactionService.find(encounterSearchParameters)).thenReturn(et1);
        when(bahmniEncounterTransactionMapper.map(et1, false)).thenReturn(new BahmniEncounterTransaction(et1));

        bahmniEncounterController = new BahmniEncounterController(null, emrEncounterService, null, bahmniEncounterTransactionService, bahmniEncounterTransactionMapper);

        BahmniEncounterTransaction bahmniEncounterTransaction = bahmniEncounterController.find(encounterSearchParameters);

        assertEquals(et1.getEncounterUuid(), bahmniEncounterTransaction.getEncounterUuid());
    }

    @Test
    public void should_return_empty_encounter_transaction_if_there_are_no_encounters_exists() throws Exception {
        BahmniEncounterSearchParameters encounterSearchParameters = new BahmniEncounterSearchParameters();
        encounterSearchParameters.setIncludeAll(false);

        when(emrEncounterService.find(encounterSearchParameters)).thenReturn(null);
        when(bahmniEncounterTransactionMapper.map(any(EncounterTransaction.class), anyBoolean())).thenReturn(new BahmniEncounterTransaction(new EncounterTransaction()));

        bahmniEncounterController = new BahmniEncounterController(null, emrEncounterService, null, bahmniEncounterTransactionService, bahmniEncounterTransactionMapper);
        BahmniEncounterTransaction bahmniEncounterTransactions = bahmniEncounterController.find(encounterSearchParameters);

        assertNull(bahmniEncounterTransactions.getEncounterUuid());
    }

}