package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataDrugService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ReferenceDataDrugServiceImplIT extends BaseModuleWebContextSensitiveTest{
    @Autowired
    private ReferenceDataDrugService referenceDataDrugService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("drugSetup.xml");
    }

    @Test
    public void create_new_drug_with_all_fields() throws Exception {
        Drug drug = new Drug();
        drug.setName("New Drug");
        drug.setGenericName("Drug Concept name");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Tablet");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
        assertFalse(savedDrug.getCombination());
        assertEquals("New Drug", savedDrug.getName());
        assertEquals("Drug Concept name", savedDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, savedDrug.getConcept().getConceptClass().getUuid());
        assertEquals("Tablet", savedDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals("Very Strong", savedDrug.getStrength());
        assertTrue(savedDrug.getMaximumDailyDose().equals(drug.doubleMaximumDose()));
        assertTrue(savedDrug.getMinimumDailyDose().equals(drug.doubleMinimumDose()));
    }

    @Test
    public void existing_drug_new_concept_new_dosage_form() throws Exception {
        Drug drug = new Drug();
        drug.setName("Existing Drug");
        drug.setGenericName("Drug Concept name");
        drug.setMinimumDose("12");
        drug.setMaximumDose("15");
        drug.setCombination(false);
        drug.setStrength("Very Strong");
        drug.setDosageForm("Capsule");
        org.openmrs.Drug savedDrug = referenceDataDrugService.saveDrug(drug);
        assertFalse(savedDrug.getCombination());
        assertEquals("Existing Drug", savedDrug.getName());
        assertEquals("Drug Concept name", savedDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, savedDrug.getConcept().getConceptClass().getUuid());
        assertEquals("Capsule", savedDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals("Very Strong", savedDrug.getStrength());
        assertTrue(savedDrug.getMaximumDailyDose().equals(drug.doubleMaximumDose()));
        assertTrue(savedDrug.getMinimumDailyDose().equals(drug.doubleMinimumDose()));
    }

    @Test
    public void existing_drug_existing_concept_new_dosage_form() throws Exception {
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
    public void new_drug_existing_concept() throws Exception {
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
    public void same_drug_multiple_times() throws Exception {
        Drug drug = new Drug();
        drug.setName("New Drug");
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
        assertEquals(savedDrug1.getUuid(), savedDrug2.getUuid());
        drug.setGenericName("Random Drug Concept");
        savedDrug2 = referenceDataDrugService.saveDrug(drug);
        assertEquals(savedDrug1.getUuid(), savedDrug2.getUuid());
    }
}