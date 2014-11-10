package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.test.builder.DrugBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;

import static org.junit.Assert.*;

public class DrugMetaDataMapperTest {

    private DrugMetaDataMapper drugMetaDataMapper;
    private ConceptClass drugConceptClass;

    @Before
    public void setUp() throws Exception {
        drugMetaDataMapper = new DrugMetaDataMapper();
        drugConceptClass = new ConceptClass();
        drugConceptClass.setUuid(ConceptClass.DRUG_UUID);
    }

    @Test
    public void create_new_drug_if_existing_drug_is_null() throws Exception {
        DrugMetaData drugMetaData = new DrugMetaData(null, null, null, drugConceptClass);
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertNotNull(conceptDrug);
        assertNotNull(conceptDrug.getConcept());
        assertEquals(ConceptClass.DRUG_UUID, conceptDrug.getConcept().getConceptClass().getUuid());
        assertNull(conceptDrug.getDosageForm());
    }

    @Test
    public void create_new_drug_with_existing_concept() throws Exception {
        Concept drugConcept = new ConceptBuilder().withName("Drug Concept").withClassUUID(ConceptClass.DRUG_UUID).build();
        DrugMetaData drugMetaData = new DrugMetaData(null, drugConcept, null, drugConceptClass);
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertNotNull(conceptDrug);
        assertNotNull(conceptDrug.getConcept());
        assertEquals(drugConcept, conceptDrug.getConcept());
        assertEquals(ConceptClass.DRUG_UUID, conceptDrug.getConcept().getConceptClass().getUuid());
        assertNull(conceptDrug.getDosageForm());
    }

    @Test
    public void create_new_drug_with_dosage_form_concept() throws Exception {
        Concept tablet = new ConceptBuilder().withName("Tablet").build();
        DrugMetaData drugMetaData = new DrugMetaData(null, null, tablet, drugConceptClass);
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertNotNull(conceptDrug);
        assertNotNull(conceptDrug.getConcept());
        assertNotNull(conceptDrug.getDosageForm());
        assertEquals("Tablet", conceptDrug.getDosageForm().getName(Context.getLocale()).getName());
    }

    @Test
    public void create_new_drug_with_dosage_form_and_existing_concept() throws Exception {
        Concept tablet = new ConceptBuilder().withName("Tablet").build();
        Concept drugConcept = new ConceptBuilder().withName("Drug Concept").withClassUUID(ConceptClass.DRUG_UUID).build();
        DrugMetaData drugMetaData = new DrugMetaData(null, drugConcept, tablet, drugConceptClass);
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertNotNull(conceptDrug);
        assertNotNull(conceptDrug.getConcept());
        assertEquals("Drug Concept", conceptDrug.getConcept().getName(Context.getLocale()).getName());
        assertNotNull(conceptDrug.getDosageForm());
        assertEquals("Tablet", conceptDrug.getDosageForm().getName(Context.getLocale()).getName());
    }

    @Test
    public void update_dosage_form_on_existing_drug() throws Exception {
        Drug existingDrug = new DrugBuilder().withConcept("Drug Concept").withDosageForm("Tablet").build();
        Concept capsule = new ConceptBuilder().withName("Capsule").build();
        DrugMetaData drugMetaData = new DrugMetaData(existingDrug, null, capsule, drugConceptClass);
        assertEquals("Tablet", existingDrug.getDosageForm().getName(Context.getLocale()).getName());
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertEquals("Drug Concept", conceptDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals("Capsule", conceptDrug.getDosageForm().getName(Context.getLocale()).getName());
    }

    @Test
    public void update_drug_concept_on_existing_drug() throws Exception {
        Drug existingDrug = new DrugBuilder().withConcept("Drug Concept").withDosageForm("Tablet").build();
        Concept drugConcept = new ConceptBuilder().withName("New Concept").withClassUUID(ConceptClass.DRUG_UUID).build();
        DrugMetaData drugMetaData = new DrugMetaData(existingDrug, drugConcept, null, drugConceptClass);
        assertEquals("Drug Concept", existingDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals("Tablet", existingDrug.getDosageForm().getName(Context.getLocale()).getName());
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertEquals("New Concept", conceptDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals("Tablet", conceptDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, conceptDrug.getConcept().getConceptClass().getUuid());
    }

    @Test
    public void update_all_fields_on_existing_drug() throws Exception {
        Drug existingDrug = new DrugBuilder().withConcept("Drug Concept").withDosageForm("Tablet").build();
        Concept capsule = new ConceptBuilder().withName("Capsule").build();
        Concept drugConcept = new ConceptBuilder().withName("New Concept").withClassUUID(ConceptClass.DRUG_UUID).build();
        DrugMetaData drugMetaData = new DrugMetaData(existingDrug, drugConcept, capsule, drugConceptClass);
        assertEquals("Drug Concept", existingDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals("Tablet", existingDrug.getDosageForm().getName(Context.getLocale()).getName());
        Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        assertEquals("New Concept", conceptDrug.getConcept().getName(Context.getLocale()).getName());
        assertEquals("Capsule", conceptDrug.getDosageForm().getName(Context.getLocale()).getName());
        assertEquals(ConceptClass.DRUG_UUID, conceptDrug.getConcept().getConceptClass().getUuid());
    }
}