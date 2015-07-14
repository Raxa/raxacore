package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrderSaveCommandImplTest {
    @Mock
    private AdministrationService adminService;

    public static final String DAY_DURATION_UNIT = "Day";
    public static final String ONCE_A_DAY_CONCEPT_NAME = "Once A Day";
    public static final String SNOMED_CT_DAYS_CODE = "258703001";


    OrderSaveCommandImpl orderSaveCommand;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        orderSaveCommand = new OrderSaveCommandImpl(adminService);
    }

    @Test
    public void ShouldSetAutoExpireDateForTestOrders(){
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        List<EncounterTransaction.Order> testOrders = Arrays.asList(new EncounterTransaction.Order());
        bahmniEncounterTransaction.setOrders(testOrders);
        when(adminService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");

        orderSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(DateTime.now().plusMinutes(60).toDate().toString(), bahmniEncounterTransaction.getOrders().get(0).getAutoExpireDate().toString());
    }



}
