package org.bahmni.module.referencedata.labconcepts.service;

import org.bahmni.module.referencedata.labconcepts.model.ConceptMetaData;

public interface ConceptMetaDataService {
    public ConceptMetaData getConceptMetaData(String conceptName, String conceptUuid, String conceptClass, String conceptDatatype);
}
