package org.bahmni.module.referencedata.labconcepts.mapper;


import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.*;

public class ConceptMapper {
    public ConceptMapper() {
    }

    public org.openmrs.Concept map(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, List<ConceptAnswer> answers, org.openmrs.Concept existingConcept) {
        double sortWeight = 0.0;
        org.openmrs.Concept concept = mapConcept(conceptData, conceptClass, existingConcept);
        for (String conceptName : conceptData.getSynonyms()) {
            concept = addConceptName(concept, getConceptName(conceptName));
        }
        concept.setDatatype(conceptDatatype);
        removeConceptAnswers(concept);
        for (ConceptAnswer answer : answers) {
            sortWeight++;
            addAnswer(concept, answer, sortWeight);
        }
        if(conceptDatatype.isNumeric()){
            concept = addConceptNumeric(concept, conceptData);
        }
        return concept;
    }

    private void removeConceptAnswers(org.openmrs.Concept concept) {
        Collection<ConceptAnswer> answers = concept.getAnswers();
        answers.clear();
        concept.setAnswers(answers);
    }

    private org.openmrs.Concept addConceptNumeric(org.openmrs.Concept concept, Concept conceptData) {
        ConceptNumeric conceptNumeric = new ConceptNumeric(concept);
        conceptNumeric.setUnits(conceptData.getUnits());
        setHiNormal(conceptData, conceptNumeric);
        setLowNormal(conceptData, conceptNumeric);
        return conceptNumeric;
    }

    private void setLowNormal(Concept conceptData, ConceptNumeric conceptNumeric) {
        String lowNormal = conceptData.getLowNormal();
        if(!StringUtils.isBlank(lowNormal)){
            conceptNumeric.setLowNormal(Double.valueOf(lowNormal));
        }
    }

    private void setHiNormal(Concept conceptData, ConceptNumeric conceptNumeric) {
        String hiNormal = conceptData.getHiNormal();
        if(!StringUtils.isBlank(hiNormal)){
            conceptNumeric.setHiNormal(Double.valueOf(hiNormal));
        }
    }

    private org.openmrs.Concept addAnswer(org.openmrs.Concept concept, ConceptAnswer answer, double sortWeight) {
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            if (conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals(answer.getAnswerConcept().getName(Context.getLocale()).getName())) {
                return concept;
            }
        }
        answer.setSortWeight(sortWeight);
        concept.addAnswer(answer);
        return concept;
    }

    public Concept map(org.openmrs.Concept concept) {
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
        String conceptDatatype = concept.getDatatype().getName();
        List<String> conceptSynonyms = getSynonyms(concept);
        List<String> conceptAnswers = getAnswers(concept);
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        if (conceptMappings != null && conceptMappings.size() > 0) {
            ConceptMap conceptMap = conceptMappings.iterator().next();
            conceptReferenceTermCode = conceptMap.getConceptReferenceTerm().getCode();
            conceptReferenceTermSource = conceptMap.getConceptReferenceTerm().getConceptSource().getName();
            conceptReferenceTermRelationship = conceptMap.getConceptMapType().getName();
        }
        String uuid = concept.getUuid();
        return new Concept(uuid, name, conceptDescription, conceptClass, conceptShortname,
                conceptReferenceTermCode, conceptReferenceTermRelationship, conceptReferenceTermSource,
                conceptDatatype, conceptSynonyms, conceptAnswers, conceptDatatype);
    }

    private List<String> getAnswers(org.openmrs.Concept concept) {
        List<String> answers = new ArrayList<>();
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            answers.add(conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName());
        }
        return answers;
    }

    private List<String> getSynonyms(org.openmrs.Concept concept) {
        List<String> synonyms = new ArrayList<>();
        for (ConceptName synonym : concept.getSynonyms()) {
            synonyms.add(synonym.getName());
        }
        return synonyms;
    }

    public List<Concept> mapAll(org.openmrs.Concept concept) {
        List<Concept> conceptList = new ArrayList<>();
        for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
            conceptList.addAll(mapAll(conceptAnswer.getAnswerConcept()));
        }
        conceptList.add(map(concept));
        return conceptList;
    }

}
