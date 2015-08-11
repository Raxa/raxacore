package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.BaseIntegrationTest;
import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataDrugService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ReferenceDataDrugServiceImplIT extends BaseIntegrationTest {
    
    @Autowired
    private ReferenceDataDrugService referenceDataDrugService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("drugSetup.xml");
    }

    @Test(expected = APIException.class)
    public void create_new_drug_with_new_concept_new_dosage_form_should_fail() throws Exception {
        Drug drug = new Drug();
        drug.setName("New Drug");
        drug.setGenericName("Drug Concept name");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Unknown123");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
    }

    @Test(expected=APIException.class)
    public void existing_drug_existing_concept_new_dosage_form_should_fail() throws Exception {
        Drug drug = new Drug();
        drug.setName("Existing Drug");
        drug.setGenericName("Old Drug Concept");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Unknown123");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
    }

    @Test
    public void existing_drug_existing_concept_existing_dosage_form() throws Exception {
        Drug drug = new Drug();
        drug.setName("Existing Drug");
        drug.setGenericName("Old Drug Concept");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Capsule");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
        assertFalse(savedDrug.getCombination());
        assertEquals("Existing Drug", savedDrug.getName());
        assertEquals("Old Drug Concept", savedDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, savedDrug.getConcept().getConceptClass().getUuid());
        assertEquals("Capsule", savedDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals("Very Strong", savedDrug.getStrength());
        assertTrue(savedDrug.getMaximumDailyDose().equals(drug.doubleMaximumDose()));
        assertTrue(savedDrug.getMinimumDailyDose().equals(drug.doubleMinimumDose()));
    }


    @Test
    public void new_drug_existing_concept_existing_dosage_form() throws Exception {
        Drug drug = new Drug();
        drug.setName("New Drug");
        drug.setGenericName("Old Drug Concept");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Capsule");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
        assertFalse(savedDrug.getCombination());
        assertEquals("New Drug", savedDrug.getName());
        assertEquals("Old Drug Concept", savedDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, savedDrug.getConcept().getConceptClass().getUuid());
        assertEquals("Capsule", savedDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals("Very Strong", savedDrug.getStrength());
        assertTrue(savedDrug.getMaximumDailyDose().equals(drug.doubleMaximumDose()));
        assertTrue(savedDrug.getMinimumDailyDose().equals(drug.doubleMinimumDose()));
    }

    @Test
    @Ignore
    public void same_drug_multiple_times() throws Exception {
        Drug drug = new Drug();
        drug.setName("NEW DRUG");
        drug.setGenericName("Old Drug Concept");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Capsule");
        org.openmrs.Drug savedDrug1 = referenceDataDrugService.saveDrug(drug);
        org.openmrs.Drug savedDrug2 = referenceDataDrugService.saveDrug(drug);
        assertEquals(savedDrug1.getUuid(), savedDrug2.getUuid());

        drug.setDosageForm("Tablet");
        savedDrug2 = referenceDataDrugService.saveDrug(drug);
        assertNotEquals(savedDrug1.getUuid(), savedDrug2.getUuid());

        drug.setDosageForm("Capsule");
        drug.setGenericName("Random Drug Concept");
        savedDrug2 = referenceDataDrugService.saveDrug(drug);
        assertEquals(savedDrug1.getUuid(), savedDrug2.getUuid());
        assertEquals("Random Drug Concept",savedDrug2.getConcept().getName().getName()); //Updated one
    }
}