package org.bahmni.module.referencedata.labconcepts.mapper;


import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;

import java.util.Set;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.constructDescription;
import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptName;

public class ConceptMapper {
    public ConceptMapper() {
    }

    public org.openmrs.Concept map(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Set<ConceptAnswer> answers, org.openmrs.Concept existingConcept) {
        org.openmrs.Concept concept = new org.openmrs.Concept();
        if (existingConcept != null) {
            concept = existingConcept;
        }
        String displayName = conceptData.getDisplayName();
        concept = addConceptName(concept, getConceptName(conceptData.getUniqueName(), ConceptNameType.FULLY_SPECIFIED));
        if (displayName != null) {
            concept = addConceptName(concept, getConceptName(conceptData.getDisplayName(), ConceptNameType.SHORT));
        }
        for (String conceptName : conceptData.getSynonyms()) {
            concept = addConceptName(concept, getConceptName(conceptName));
        }
        for (ConceptAnswer answer : answers) {
            concept.addAnswer(answer);
        }
        if (conceptData.getDescription() != null && concept.getDescription() != null) {
            concept.getDescription().setDescription(conceptData.getDescription());
        } else if (conceptData.getDescription() != null) {
            concept.addDescription(constructDescription(conceptData.getDescription()));
        }
        concept.setConceptClass(conceptClass);
        concept.setDatatype(conceptDatatype);
        return concept;
    }

    private org.openmrs.Concept addConceptName(org.openmrs.Concept concept, ConceptName conceptName) {
        for (ConceptName name : concept.getNames()) {
            if (name.getName().equals(conceptName.getName())) {
                return concept;
            }
        }
        concept.addName(conceptName);
        return concept;
    }

}
