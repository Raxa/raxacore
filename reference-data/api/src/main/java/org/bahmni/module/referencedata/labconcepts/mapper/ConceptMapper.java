package org.bahmni.module.referencedata.labconcepts.mapper;


import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.openmrs.*;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.*;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptName;

public class ConceptMapper {
    public ConceptMapper() {
    }

    public org.openmrs.Concept map(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Set<ConceptAnswer> answers, org.openmrs.Concept existingConcept) {
        org.openmrs.Concept concept = new org.openmrs.Concept();
        if(existingConcept != null){
            concept = existingConcept;
        }
        String displayName = conceptData.getDisplayName();
        concept.addName(getConceptName(conceptData.getUniqueName(), ConceptNameType.FULLY_SPECIFIED));
        if(displayName != null){
            concept.addName(getConceptName(conceptData.getDisplayName(), ConceptNameType.SHORT));
        }
        for (String conceptName : conceptData.getSynonyms()) {
            concept.addName(getConceptName(conceptName));
        }
        for (ConceptAnswer answer : answers) {
            concept.addAnswer(answer);
        }
        concept.setDescriptions(MapperUtils.constructDescription(conceptData.getDescription()));
        concept.setConceptClass(conceptClass);
        concept.setDatatype(conceptDatatype);
        return concept;
    }
}
