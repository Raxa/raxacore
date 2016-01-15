package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.impl.Dose;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
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
        when(doseCalculatorService.calculateDose("patientUuid", 5.0, Dose.CalculatedDoseUnit.mg_per_m2))
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
