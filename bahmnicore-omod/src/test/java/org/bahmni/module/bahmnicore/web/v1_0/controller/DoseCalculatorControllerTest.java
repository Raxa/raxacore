package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.impl.DoseCalculatorFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DoseCalculatorControllerTest {

    @Mock
    private DoseCalculatorService doseCalculatorService;

    private DoseCalculatorController doseCalculatorController;

    @Before
    public void setup() {
        initMocks(this);
        doseCalculatorController = new DoseCalculatorController(doseCalculatorService);
    }

    @Test
    public void shouldGetCorrectCalculatedDoseForGivenRule() throws Exception {
        when(doseCalculatorService.calculateDose("patientUuid", 5.0, DoseCalculatorFactory.DoseUnit.mg_per_m2)).thenReturn(10.0);

        Double calculatedDose = doseCalculatorController.calculateDose("patientUuid", 5.0, "mg/m2");

        assertEquals(10.0,calculatedDose,0.0);
    }

    @Test
    public void shouldThrowExceptionWhenDoseUnitsIsNotValid() throws Exception{
        Double calculatedDose;
        try {
            calculatedDose = doseCalculatorController.calculateDose("patientUuid", 5.0, "randomUnit");
        } catch (Exception e) {
            calculatedDose = null;
            Assert.assertEquals("Dose Calculator not found for given doseUnits (randomUnit).", e.getMessage());
        }
        Assert.assertEquals(null, calculatedDose);
    }
}