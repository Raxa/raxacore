package org.bahmni.module.referncedatafeedclient.service;

import org.bahmni.module.referncedatafeedclient.domain.ReferenceDataConcept;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

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
        addOrUpdateName(concept, referenceDataConcept.getName(), ConceptNameType.FULLY_SPECIFIED);
        if(referenceDataConcept.getShortName() != null) {
            addOrUpdateName(concept, referenceDataConcept.getShortName(), ConceptNameType.SHORT);
        }
        if(referenceDataConcept.getDescription() != null) {
            addOrUpdateDescription(concept, referenceDataConcept.getDescription());
        }
        addOrRemoveSetMembers(concept, referenceDataConcept.getSetMemberUuids());
        concept.setRetired(referenceDataConcept.isRetired());
        concept.setSet(referenceDataConcept.isSet());
        return conceptService.saveConcept(concept);
    }

    private void addOrRemoveSetMembers(Concept concept, Set<String> setMemberUuids) {
        for (String uuid : setMemberUuids) {
            Concept childConcept = conceptService.getConceptByUuid(uuid);
            if(!concept.getSetMembers().contains(childConcept))
                concept.addSetMember(childConcept);
        }
        for (ConceptSet conceptSet : new ArrayList<>(concept.getConceptSets())) {
            if(!setMemberUuids.contains(conceptSet.getConcept().getUuid())){
                concept.getConceptSets().remove(conceptSet);
            }
        }
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

    private void addOrUpdateName(Concept concept, String name, ConceptNameType type) {
        ConceptName conceptName = concept.getName(locale, type, null);
        if(conceptName != null) {
            conceptName.setName(name);
        } else {
            ConceptName newName = new ConceptName(name, locale);
            newName.setConceptNameType(type);
            concept.addName(newName);
        }
    }
}
