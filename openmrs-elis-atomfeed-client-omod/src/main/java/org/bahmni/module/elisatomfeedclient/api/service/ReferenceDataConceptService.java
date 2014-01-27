package org.bahmni.module.elisatomfeedclient.api.service;

import org.bahmni.module.elisatomfeedclient.api.domain.ReferenceDataConcept;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ReferenceDataConceptService {
    private ConceptService conceptService;

    @Autowired
    public ReferenceDataConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public Concept saveConcept(ReferenceDataConcept referenceDataConcept) {
        Concept concept = conceptService.getConceptByUuid(referenceDataConcept.getUuid());
        if(concept == null) {
            concept = new Concept();
            concept.setUuid(referenceDataConcept.getUuid());
            concept.setDatatype(conceptService.getConceptDatatypeByUuid(referenceDataConcept.getDataTypeUuid()));
            concept.setConceptClass(conceptService.getConceptClassByName(referenceDataConcept.getClassName()));
        }
        addOrUpdateName(referenceDataConcept, concept);
        addOrUpdateDescription(referenceDataConcept, concept);
        return conceptService.saveConcept(concept);
    }

    private void addOrUpdateDescription(ReferenceDataConcept referenceDataConcept, Concept concept) {
        ConceptDescription description = concept.getDescription(Locale.ENGLISH);
        if(description != null) {
            description.setDescription(referenceDataConcept.getDescription());
        } else {
            concept.addDescription(new ConceptDescription(referenceDataConcept.getDescription(), Locale.ENGLISH));
        }
    }

    private void addOrUpdateName(ReferenceDataConcept referenceDataConcept, Concept concept) {
        ConceptName name = concept.getName(Locale.ENGLISH);
        if(name != null) {
            name.setName(referenceDataConcept.getName());
        } else {
            concept.addName(new ConceptName(referenceDataConcept.getName(), Locale.ENGLISH));
        }
    }
}
