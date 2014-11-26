package org.bahmni.module.referencedata.labconcepts.mapper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;
import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

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


    public Drug map(org.openmrs.Drug conceptDrug) {
        Drug drug = new Drug();
        drug.setName(conceptDrug.getName());
        drug.setShortName(getShortNameFrom(conceptDrug.getConcept()));
        drug.setGenericName(getNameFrom(conceptDrug.getConcept()));
        drug.setDosageForm(getNameFrom(conceptDrug.getDosageForm()));
        drug.setStrength(conceptDrug.getStrength());
        drug.setUuid(conceptDrug.getUuid());
        drug.setCombination(conceptDrug.getCombination());
        drug.setMaximumDose(conceptDrug.getMaximumDailyDose()==null?"":conceptDrug.getMaximumDailyDose().toString());
        drug.setMinimumDose(conceptDrug.getMinimumDailyDose()==null?"":conceptDrug.getMinimumDailyDose().toString());
        return drug;
    }

    private String getShortNameFrom(Concept concept) {
        if (concept != null && concept.getShortNameInLocale(Context.getLocale()) != null) {
            return concept.getShortNameInLocale(Context.getLocale()).getName();
        }
        return null;
    }

    private String getNameFrom(Concept concept) {
        if (concept != null && concept.getName(Context.getLocale()) != null) {
            return concept.getName(Context.getLocale()).getName();
        }
        return null;
    }
}