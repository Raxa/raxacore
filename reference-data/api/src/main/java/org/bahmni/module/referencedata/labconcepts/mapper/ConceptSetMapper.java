package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptNameType;

import java.util.ArrayList;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.constructDescription;
import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.getConceptName;

public class ConceptSetMapper {

    public Concept map(ConceptSet conceptSet, ConceptClass conceptClass, ArrayList<Concept> childConcepts) {
        Concept concept = new Concept();
        concept.addName(getConceptName(conceptSet.getUniqueName(), ConceptNameType.FULLY_SPECIFIED));
        if (conceptSet.getDisplayName() != null) {
            concept.addName(getConceptName(conceptSet.getDisplayName(), ConceptNameType.SHORT));
        }
        if(conceptSet.getDescription() != null){
            concept.addDescription(constructDescription(conceptSet.getDescription()));
        }
        concept.setConceptClass(conceptClass);
        for (Concept childConcept : childConcepts) {
            concept.addSetMember(childConcept);
        }
        return concept;
    }
}
