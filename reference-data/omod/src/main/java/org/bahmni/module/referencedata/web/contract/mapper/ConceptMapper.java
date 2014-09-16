package org.bahmni.module.referencedata.web.contract.mapper;


import org.bahmni.module.referencedata.web.contract.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.bahmni.module.referencedata.web.contract.mapper.MapperUtils.constructDescription;

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
        concept.setDescriptions(constructDescription(conceptData.getDescription()));
        concept.setConceptClass(conceptClass);
        concept.setDatatype(conceptDatatype);
        return concept;
    }
}
