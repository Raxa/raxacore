package org.bahmni.module.referencedata.helper;

import org.openmrs.Concept;
import org.bahmni.module.referencedata.contract.ConceptDetails;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.emrapi.utils.HibernateLazyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public  class ConceptHelper {
    private ConceptService conceptService;

    @Autowired
    public ConceptHelper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }


    public List<Concept> getConceptsForNames(Collection<String> conceptNames){
        List<Concept> concepts = new ArrayList<>();
        if(conceptNames!= null){
            for (String conceptName : conceptNames) {
                Concept concept = conceptService.getConceptByName(conceptName.replaceAll("%20", " "));
                if(concept != null) {
                    concepts.add(concept);
                }
            }
        }
        return concepts;
    }

    public Set<ConceptDetails> getLeafConceptDetails(List<String> obsConcepts, boolean withoutAttributes) {
        if(obsConcepts != null && !obsConcepts.isEmpty()){
            Set<ConceptDetails> leafConcepts = new LinkedHashSet<>();
            for (String conceptName : obsConcepts) {
                Concept concept = conceptService.getConceptByName(conceptName);
                addLeafConcepts(concept, null, leafConcepts, withoutAttributes);
            }
            return leafConcepts;
        }
        return Collections.EMPTY_SET;
    }

    protected void addLeafConcepts(Concept rootConcept, Concept parentConcept, Set<ConceptDetails> leafConcepts, boolean withoutAttributes) {
        if(rootConcept != null){
            if(rootConcept.isSet()){
                for (Concept setMember : rootConcept.getSetMembers()) {
                    addLeafConcepts(setMember,rootConcept,leafConcepts, withoutAttributes);
                }
            }
            else if(!shouldBeExcluded(rootConcept)){
                Concept conceptToAdd = rootConcept;
                if(parentConcept != null && ! withoutAttributes){
                    if(ETObsToBahmniObsMapper.CONCEPT_DETAILS_CONCEPT_CLASS.equals(parentConcept.getConceptClass().getName())){
                        conceptToAdd = parentConcept;
                    }
                }
                leafConcepts.add(createConceptDetails(conceptToAdd));
            }
        }
    }

    private ConceptDetails createConceptDetails(Concept conceptToAdd) {
        Concept concept = new HibernateLazyLoader().load(conceptToAdd);

        String fullName = getConceptName(concept, ConceptNameType.FULLY_SPECIFIED);
        String shortName = getConceptName(concept, ConceptNameType.SHORT);
        ConceptDetails conceptDetails = new ConceptDetails();
        conceptDetails.setName(shortName == null ? fullName : shortName);
        conceptDetails.setFullName(fullName);
        if (concept.isNumeric()){
            ConceptNumeric numericConcept = (ConceptNumeric) concept;
            conceptDetails.setUnits(numericConcept.getUnits());
            conceptDetails.setHiNormal(numericConcept.getHiNormal());
            conceptDetails.setLowNormal(numericConcept.getLowNormal());
        }
        return conceptDetails;
    }

    protected String getConceptName(Concept rootConcept, ConceptNameType conceptNameType) {
        String conceptName = null;
        ConceptName name = rootConcept.getName(Context.getLocale(), conceptNameType, null);
        if(name != null){
            conceptName  = name.getName();
        }
        return conceptName;
    }

    private boolean shouldBeExcluded(Concept rootConcept) {
        return ETObsToBahmniObsMapper.ABNORMAL_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName()) ||
                ETObsToBahmniObsMapper.DURATION_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName());
    }

    public Set<ConceptDetails> getConceptDetails(List<String> conceptNames) {
        LinkedHashSet<ConceptDetails> conceptDetails = new LinkedHashSet<>();
        for (String conceptName : conceptNames) {
            Concept conceptByName = conceptService.getConceptByName(conceptName);
            if (conceptByName != null){
                conceptDetails.add(createConceptDetails(conceptByName));
            }
        }
        return conceptDetails;
    }
}
