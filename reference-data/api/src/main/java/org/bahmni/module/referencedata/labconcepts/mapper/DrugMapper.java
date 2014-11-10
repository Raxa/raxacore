package org.bahmni.module.referencedata.labconcepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;
import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptName;

public class DrugMapper {


    private final DrugMetaDataMapper drugMetaDataMapper;

    public DrugMapper() {
        drugMetaDataMapper = new DrugMetaDataMapper();
    }

    public org.openmrs.Drug map(Drug drug, DrugMetaData drugMetaData) {
        org.openmrs.Drug conceptDrug = drugMetaDataMapper.map(drugMetaData);
        conceptDrug.setName(drug.getName());
        MapperUtils.addConceptName(conceptDrug.getConcept(), getConceptName(drug.getGenericName(), ConceptNameType.FULLY_SPECIFIED));
        if(conceptDrug.getDosageForm() == null && !StringUtils.isBlank(drug.getDosageForm())){
            Concept dosageForm = new Concept();
            dosageForm.addName(getConceptName(drug.getDosageForm(), ConceptNameType.FULLY_SPECIFIED));
            conceptDrug.setDosageForm(dosageForm);
        }
        conceptDrug.setCombination(drug.isCombination());
        conceptDrug.setStrength(drug.getStrength());
        conceptDrug.setMaximumDailyDose(drug.doubleMaximumDose());
        conceptDrug.setMinimumDailyDose(drug.doubleMinimumDose());
        return conceptDrug;
    }
}