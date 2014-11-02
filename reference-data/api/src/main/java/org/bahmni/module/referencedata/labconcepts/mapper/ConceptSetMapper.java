package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.contract.Concepts;
import org.openmrs.*;
import org.openmrs.api.ConceptsLockedException;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.*;

public class ConceptSetMapper {

    private final ConceptMapper conceptMapper;

    public ConceptSetMapper() {
        conceptMapper = new ConceptMapper();
    }

    public Concept map(ConceptSet conceptSet, List<Concept> childConcepts, ConceptClass conceptClass, ConceptDatatype conceptDatatype, Concept existingConcept) {
        Concept concept = mapConcept(conceptSet, conceptClass, existingConcept);
        concept.setSet(true);
        concept.setDatatype(conceptDatatype);
        removeAllSetMembers(concept);
        for (Concept childConcept : childConcepts) {
            addSetMember(concept, childConcept);
        }
        return concept;
    }

    private void removeAllSetMembers(Concept concept) {
        Collection<org.openmrs.ConceptSet> conceptSets = concept.getConceptSets();
        conceptSets.clear();
        concept.setConceptSets(conceptSets);
    }

    private org.openmrs.Concept addSetMember(Concept concept, Concept childConcept) {
        if (ifChildExists(concept, childConcept)) return concept;
        concept.addSetMember(childConcept);
        return concept;
    }

    private boolean ifChildExists(Concept concept, Concept childConcept) {
        for (Concept child  : concept.getSetMembers()) {
            if (child.getName(Context.getLocale()).getName().equals(childConcept.getName(Context.getLocale()).getName())) {
                return true;
            }
        }
        return false;
    }

    public ConceptSet map(Concept concept) {
        String conceptReferenceTermCode = null, conceptReferenceTermSource = null,
                conceptReferenceTermRelationship = null, conceptDescription = null, conceptShortname = null;
        String name = concept.getName(Context.getLocale()).getName();
        ConceptDescription description = concept.getDescription(Context.getLocale());
        if (description != null) {
            conceptDescription = description.getDescription();
        }
        ConceptName shortName = concept.getShortNameInLocale(Context.getLocale());
        if (shortName != null) {
            conceptShortname = shortName.getName();
        }
        String conceptClass = concept.getConceptClass().getName();
        List<String> children = getSetMembers(concept);
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        if (conceptMappings != null && conceptMappings.size() > 0) {
            ConceptMap conceptMap = conceptMappings.iterator().next();
            conceptReferenceTermCode = conceptMap.getConceptReferenceTerm().getCode();
            conceptReferenceTermSource = conceptMap.getConceptReferenceTerm().getConceptSource().getName();
            conceptReferenceTermRelationship = conceptMap.getConceptMapType().getName();
        }
        String uuid = concept.getUuid();
        ConceptSet conceptSet = new ConceptSet(uuid, name, conceptDescription, conceptClass, conceptShortname, conceptReferenceTermCode, conceptReferenceTermRelationship, conceptReferenceTermSource, children);
        return conceptSet;
    }

    private List<String> getSetMembers(Concept concept) {
        List<String> setMembers = new ArrayList<>();
        for (Concept setMember : concept.getSetMembers()) {
            setMembers.add(setMember.getName(Context.getLocale()).getName());
        }
        return setMembers;
    }

    public Concepts mapAll(Concept concept) {
        List<ConceptSet> conceptSetList = new ArrayList<>();
        List<org.bahmni.module.referencedata.labconcepts.contract.Concept> conceptList = new ArrayList<>();
        for(Concept setMember: concept.getSetMembers()){
            if (setMember.isSet()) {
                Concepts concepts = mapAll(setMember);
                conceptSetList.addAll(concepts.getConceptSetList());
                conceptList.addAll(concepts.getConceptList());
            } else {
                conceptList.addAll(conceptMapper.mapAll(setMember));
            }
        }
        conceptSetList.add(map(concept));
        Concepts concepts = new Concepts();
        concepts.setConceptList(conceptList);
        concepts.setConceptSetList(conceptSetList);
        return concepts;
    }
}
