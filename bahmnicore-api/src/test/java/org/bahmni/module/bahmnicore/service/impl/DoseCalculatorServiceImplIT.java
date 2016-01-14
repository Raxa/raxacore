package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DoseCalculatorServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private DoseCalculatorService doseCalculatorService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("doseCalculatorTestData.xml");
    }

    @Test
    public void shouldGetCalculatedDoseForAGivenRule() throws Exception {
        Double dosage = doseCalculatorService.getCalculatedDoseForRule("person_1024_uuid", 5.0, "BSA");
        assertEquals(8.65,dosage,0.01);

        dosage = doseCalculatorService.getCalculatedDoseForRule("person_1024_uuid", 5.0, "WeightBasedDose");
        assertEquals(350.0,dosage,0.01);
    }

    @Test(expected = APIException.class)
    public void shouldThrowExceptionWhenRuleNotFound() throws Exception{
        doseCalculatorService.getCalculatedDoseForRule("person_uuid", 5.0, "FSO");
    }

    @Test
    public void shouldGetCalculatedDoseForTheLatestObservations() throws Exception{
        Double dosage = doseCalculatorService.getCalculatedDoseForRule("person_1030_uuid", 5.0, "BSA");
        assertEquals(9.58,dosage,0.01);

        dosage = doseCalculatorService.getCalculatedDoseForRule("person_1030_uuid", 5.0, "WeightBasedDose");
        assertEquals(400.0,dosage,0.01);
    }

}