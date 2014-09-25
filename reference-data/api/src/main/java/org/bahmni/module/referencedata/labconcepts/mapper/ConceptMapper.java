package org.bahmni.module.referencedata.labconcepts.mapper;


import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;

import java.util.Locale;
import java.util.Set;

public class ConceptMapper {
    public ConceptMapper() {
    }

    public org.openmrs.Concept map(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Set<ConceptAnswer> answers) {
        org.openmrs.Concept concept = new org.openmrs.Concept();
        concept.setFullySpecifiedName(new ConceptName(conceptData.getUniqueName(), Locale.ENGLISH));
        String displayName = conceptData.getDisplayName();
        if(displayName != null){
            concept.setShortName(new ConceptName(displayName, Locale.ENGLISH));
        }
        concept.setAnswers(answers);
        concept.setDescriptions(MapperUtils.constructDescription(conceptData.getDescription()));
        concept.setConceptClass(conceptClass);
        concept.setDatatype(conceptDatatype);
        return concept;
    }
}
