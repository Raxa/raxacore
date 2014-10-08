package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.*;

public class ConceptSetMapper {

    public Concept map(ConceptSet conceptSet, List<Concept> childConcepts, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Concept existingConcept) {
        Concept concept = mapConcept(conceptSet, conceptClass, existingConcept);
        concept.setSet(true);
        concept.setDatatype(conceptDatatype);
        for (Concept childConcept : childConcepts) {
            addSetMember(concept, childConcept);
        }
        return concept;
    }

    private org.openmrs.Concept addSetMember(org.openmrs.Concept concept, Concept childConcept) {
        for (Concept child  : concept.getSetMembers()) {
            if (child.getName(Context.getLocale()).getName().equals(childConcept.getName(Context.getLocale()).getName())) {
                return concept;
            }
        }
        concept.addSetMember(childConcept);
        return concept;
    }
}
