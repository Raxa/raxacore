package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.APIException;

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
        when(doseCalculatorService.getCalculatedDoseForRule("patientUuid", 5.0, "BSA")).thenReturn(10.0);

        Double calculatedDose = doseCalculatorController.getCalculatedDose("patientUuid", 5.0, "BSA");

        assertEquals(10.0,calculatedDose,0.0);
    }

    @Test(expected = APIException.class)
    public void shouldThrowExceptionOnInvalidRule() throws Exception {
        when(doseCalculatorService.getCalculatedDoseForRule("patientUuid", 5.0, "FUL")).thenThrow(new APIException());

        doseCalculatorController.getCalculatedDose("patientUuid", 5.0, "FUL");
    }
}