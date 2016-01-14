package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class BSABasedDoseCalculatorIT extends BaseIntegrationTest{

    @Autowired
    private BSABasedDoseCalculator bsaBasedDoseCalculator;

    @Before
    public void setUp() throws Exception {
        executeDataSet("DoseCalculatorTestData.xml");
    }

    @Test
    public void shouldThrowExceptionHeightNotAvailableWhenHeightObsDoesNotExist() {
        Double calculatedDoseForRule;
        try {
            calculatedDoseForRule = bsaBasedDoseCalculator.calculateDose("person_1031_uuid", 5.0);
        } catch (Exception e) {
            calculatedDoseForRule=null;
            assertEquals(e.getMessage(), "Height is not available");
        }
        assertEquals(calculatedDoseForRule, null);
    }

    @Test
    public void shouldThrowExceptionWeightNotAvailableWhenWeightObsDoesNotExist() {
        Double calculatedDoseForRule;
        try {
            calculatedDoseForRule = bsaBasedDoseCalculator.calculateDose("person_1032_uuid", 5.0);
        } catch (Exception e) {
            calculatedDoseForRule = null;
            assertEquals(e.getMessage(), "Weight is not available");
        }
        assertEquals(calculatedDoseForRule, null);
    }
}