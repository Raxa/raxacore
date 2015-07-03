package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.DrugOrderUtil;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.builder.DrugOrderBuilder;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.service.OrderMetadataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestOrderSaveCommandImplTest {
    @Mock
    private AdministrationService adminService;

    public static final String DAY_DURATION_UNIT = "Day";
    public static final String ONCE_A_DAY_CONCEPT_NAME = "Once A Day";
    public static final String SNOMED_CT_DAYS_CODE = "258703001";


    TestOrderSaveCommandImpl testOrderSaveCommand;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        testOrderSaveCommand = new TestOrderSaveCommandImpl(adminService);
    }

    @Test
    public void ShouldSetAutoExpireDateForTestOrders(){
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        List<EncounterTransaction.TestOrder> testOrders = Arrays.asList(new EncounterTransaction.TestOrder());
        bahmniEncounterTransaction.setTestOrders(testOrders);
        when(adminService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");

        testOrderSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(DateTime.now().plusMinutes(60).toDate().toString(), bahmniEncounterTransaction.getTestOrders().get(0).getAutoExpireDate().toString());
    }



}
