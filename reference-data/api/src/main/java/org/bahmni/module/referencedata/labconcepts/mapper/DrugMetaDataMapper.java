package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;
import org.openmrs.Concept;
import org.openmrs.Drug;

public class DrugMetaDataMapper {

    public DrugMetaDataMapper() {
    }

    public org.openmrs.Drug map(DrugMetaData drugMetaData) {
        Drug drug = drugMetaData.getExistingDrug();
        drug.setDosageForm(drugMetaData.getDosageForm());
        drug.setConcept(drugMetaData.getDrugConcept());
        return drug;
    }
}
