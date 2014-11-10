package org.bahmni.module.referencedata.labconcepts.service;

import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;

public interface DrugMetaDataService {
    public DrugMetaData getDrugMetaData(String drugName, String drugUuid, String genericName, String dosageForm);
}
