package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.labconcepts.model.ConceptMetaData;
import org.bahmni.module.referencedata.labconcepts.service.ConceptMetaDataService;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptMetaDataServiceImpl implements ConceptMetaDataService {

    @Autowired
    private ConceptService conceptService;

    @Override
    public ConceptMetaData getConceptMetaData(String conceptName, String conceptUuid, String conceptClassName, String dataType) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptClassName);
        org.openmrs.Concept existingConcept = getExistingConcept(conceptName, conceptUuid);
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(dataType);
        return new ConceptMetaData(existingConcept, conceptDatatype, conceptClass);
    }

    private org.openmrs.Concept getExistingConcept(String uniqueName, String uuid) {
        if (uuid != null) {
            return conceptService.getConceptByUuid(uuid);
        }
        return conceptService.getConceptByName(uniqueName);
    }
}
