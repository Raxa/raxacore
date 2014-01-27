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
    private Locale locale = Locale.ENGLISH;

    @Autowired
    public ReferenceDataConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public Concept saveConcept(ReferenceDataConcept referenceDataConcept) {
        Concept concept = conceptService.getConceptByUuid(referenceDataConcept.getUuid());
        if(concept == null) {
            concept = new Concept();
            concept.setUuid(referenceDataConcept.getUuid());
        }
        concept.setDatatype(conceptService.getConceptDatatypeByUuid(referenceDataConcept.getDataTypeUuid()));
        concept.setConceptClass(conceptService.getConceptClassByName(referenceDataConcept.getClassName()));
        addOrUpdateName(concept, referenceDataConcept.getName());
        if(referenceDataConcept.getShortName() != null) {
            concept.setShortName(new ConceptName(referenceDataConcept.getShortName(), locale));
        }
        addOrUpdateDescription(concept, referenceDataConcept.getDescription());
        return conceptService.saveConcept(concept);
    }

    public void saveSetMembership(Concept parentConcept, Concept childConcept) {
        if(parentConcept.getSetMembers().contains(childConcept)) return;
        parentConcept.addSetMember(childConcept);
        conceptService.saveConcept(parentConcept);
    }

    private void addOrUpdateDescription(Concept concept, String description) {
        ConceptDescription conceptDescription = concept.getDescription(locale);
        if(conceptDescription != null) {
            conceptDescription.setDescription(description);
        } else {
            concept.addDescription(new ConceptDescription(description, locale));
        }
    }

    private void addOrUpdateName(Concept concept, String name) {
        ConceptName conceptName = concept.getFullySpecifiedName(locale);
        if(conceptName != null) {
            conceptName.setName(name);
        } else {
            concept.setFullySpecifiedName(new ConceptName(name, locale));
        }
    }
}
