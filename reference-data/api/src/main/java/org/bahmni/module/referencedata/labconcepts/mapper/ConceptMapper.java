package org.bahmni.module.referencedata.labconcepts.mapper;


import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.context.Context;

import java.util.Set;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.*;

public class ConceptMapper {
    public ConceptMapper() {
    }

    public org.openmrs.Concept map(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Set<ConceptAnswer> answers, org.openmrs.Concept existingConcept) {
        org.openmrs.Concept concept = mapConcept(conceptData, conceptClass, existingConcept);
        for (String conceptName : conceptData.getSynonyms()) {
            concept = addConceptName(concept, getConceptName(conceptName));
        }
        concept.setDatatype(conceptDatatype);
        for (ConceptAnswer answer : answers) {
            addAnswer(concept, answer);
        }
        return concept;
    }

    private org.openmrs.Concept addAnswer(org.openmrs.Concept concept, ConceptAnswer answer) {
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            if (conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals(answer.getAnswerConcept().getName(Context.getLocale()).getName())) {
                return concept;
            }
        }
        concept.addAnswer(answer);
        return concept;
    }


}
