package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.openmrs.api.context.Context;
import org.openmrs.module.rulesengine.contract.Dose;
import org.openmrs.module.rulesengine.service.DoseRuleService;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class DoseCalculatorControllerTest {

    @Mock
    private DoseRuleService doseCalculatorService;

    private DoseCalculatorController doseCalculatorController;

    @Before
    public void setup() {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(DoseRuleService.class)).thenReturn(doseCalculatorService);
        doseCalculatorController = new DoseCalculatorController();
    }

    @Test
    public void shouldGetCorrectCalculatedDoseForGivenRule() throws Exception {
        when(doseCalculatorService.calculateDose("patientUuid", 5.0, "mg/m2"))
            .thenReturn(new Dose(10.0, Dose.DoseUnit.mg));

        Dose calculatedDose = doseCalculatorController.calculateDose("patientUuid", 5.0, "mg/m2");

        assertEquals(10.0, calculatedDose.getValue(),0.0);
        assertEquals(Dose.DoseUnit.mg, calculatedDose.getDoseUnit());
    }

    @Test
    public void shouldThrowExceptionWhenDoseUnitsIsNotValid() throws Exception{
        Dose calculatedDose;
        try {
            calculatedDose = doseCalculatorController.calculateDose("patientUuid", 5.0, "randomUnit");
        } catch (Exception e) {
            calculatedDose = null;
            assertEquals("Dose Calculator not found for given doseUnits (randomUnit).", e.getMessage());
        }
        assertEquals(null, calculatedDose);
    }
}
