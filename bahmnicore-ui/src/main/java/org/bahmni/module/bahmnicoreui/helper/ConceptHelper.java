package org.bahmni.module.bahmnicoreui.helper;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

import java.util.*;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;

public  class ConceptHelper {


    private ConceptService conceptService;

    public ConceptHelper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }


    public List<Concept> getConceptsForNames(Collection<String> conceptNames){
        List<Concept> concepts = new ArrayList<>();
        if(conceptNames!= null){
            for (String conceptName : conceptNames) {
                concepts.add(conceptService.getConceptByName(conceptName.replaceAll("%20", " ")));
            }
        }
        return concepts;
    }

    public Set<String> getLeafConceptNames(List<String> obsConcepts) {
        if(obsConcepts != null && !obsConcepts.isEmpty()){
            Set<String> leafConcepts = new LinkedHashSet<>();
            for (String conceptName : obsConcepts) {
                Concept concept = conceptService.getConceptByName(conceptName);
                addLeafConcepts(concept, null, leafConcepts);
            }
            return leafConcepts;
        }
        return Collections.EMPTY_SET;
    }

    protected void addLeafConcepts(Concept rootConcept, Concept parentConcept, Collection<String> leafConcepts) {
        if(rootConcept != null){
            if(rootConcept.isSet()){
                for (Concept setMember : rootConcept.getSetMembers()) {
                    addLeafConcepts(setMember,rootConcept,leafConcepts);
                }
            }
            else if(!shouldBeExcluded(rootConcept)){
                Concept conceptToAdd = rootConcept;
                if(parentConcept != null){
                    if(ETObsToBahmniObsMapper.CONCEPT_DETAILS_CONCEPT_CLASS.equals(parentConcept.getConceptClass().getName())){
                        conceptToAdd = parentConcept;
                    }
                }
                String fullName = getConceptName(conceptToAdd, ConceptNameType.FULLY_SPECIFIED);
                String shortName = getConceptName(conceptToAdd, ConceptNameType.SHORT);
                leafConcepts.add(shortName==null?fullName:shortName);
            }
        }
    }

    protected String getConceptName(Concept rootConcept, ConceptNameType conceptNameType) {
        String conceptName = null;
        ConceptName name = rootConcept.getName(Context.getLocale(), conceptNameType, null);
        if(name != null){
            conceptName  = name.getName();
        }
        return conceptName;
    }

    protected boolean shouldBeExcluded(Concept rootConcept) {
        return ETObsToBahmniObsMapper.ABNORMAL_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName()) ||
                ETObsToBahmniObsMapper.DURATION_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName());
    }
}
