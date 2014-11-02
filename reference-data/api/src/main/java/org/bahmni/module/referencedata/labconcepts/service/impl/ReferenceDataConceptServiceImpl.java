package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.contract.Concepts;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptMapper;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptSetMapper;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptReferenceTermService;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.bahmni.module.referencedata.labconcepts.validator.ConceptValidator;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferenceDataConceptServiceImpl implements ReferenceDataConceptService {
    private ConceptService conceptService;
    private ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService;
    private ConceptMapper conceptMapper;
    private ConceptSetMapper conceptSetMapper;
    private final ConceptValidator conceptValidator;
    private List<String> notFound;

    @Autowired
    public ReferenceDataConceptServiceImpl(ConceptService conceptService, ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService) {
        this.conceptMapper = new ConceptMapper();
        this.conceptSetMapper = new ConceptSetMapper();
        this.conceptService = conceptService;
        this.referenceDataConceptReferenceTermService = referenceDataConceptReferenceTermService;
        conceptValidator = new ConceptValidator();
    }

    @Override
    public org.openmrs.Concept saveConcept(Concept conceptData) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptData.getClassName());
        org.openmrs.Concept existingConcept = getExistingConcept(conceptData.getUniqueName(), conceptData.getUuid());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptData.getDataType());
        org.openmrs.Concept mappedConcept = getConcept(conceptData, conceptClass, conceptDatatype, existingConcept);
        return conceptService.saveConcept(mappedConcept);
    }

    private org.openmrs.Concept getExistingConcept(String uniqueName, String uuid) {
        if (uuid != null) {
            return conceptService.getConceptByUuid(uuid);
        }
        return conceptService.getConceptByName(uniqueName);
    }

    @Override
    public org.openmrs.Concept saveConcept(ConceptSet conceptSet) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptSet.getClassName());
        org.openmrs.Concept existingConcept = getExistingConcept(conceptSet.getUniqueName(), conceptSet.getUuid());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptSet.getDataType());
        org.openmrs.Concept mappedConceptSet = getConceptSet(conceptSet, conceptClass, existingConcept, conceptDatatype);
        return conceptService.saveConcept(mappedConceptSet);
    }

    @Override
    public Concepts getConcept(String conceptName) {
        org.openmrs.Concept concept = conceptService.getConceptByName(conceptName);
        if (concept == null) {
            return null;
        }
        Concepts concepts = conceptSetMapper.mapAll(concept);
        return concepts;
    }

    private org.openmrs.Concept getConceptSet(ConceptSet conceptSet, ConceptClass conceptClass, org.openmrs.Concept existingConcept, ConceptDatatype conceptDatatype) {
        List<org.openmrs.Concept> setMembers = getSetMembers(conceptSet.getChildren());
        conceptValidator.validate(conceptSet, conceptClass, conceptDatatype, notFound);
        ConceptMap conceptMap = referenceDataConceptReferenceTermService.getConceptMap(conceptSet.getConceptReferenceTerm());
        org.openmrs.Concept mappedConceptSet = conceptSetMapper.map(conceptSet, setMembers, conceptClass, conceptDatatype, existingConcept);
        mappedConceptSet = conceptMapper.addConceptMap(mappedConceptSet, conceptMap);
        return mappedConceptSet;
    }

    private org.openmrs.Concept getConcept(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, org.openmrs.Concept existingConcept) {
        List<ConceptAnswer> conceptAnswers = getConceptAnswers(conceptData.getAnswers());
        conceptValidator.validate(conceptData, conceptClass, conceptDatatype, notFound);
        ConceptMap conceptMap = referenceDataConceptReferenceTermService.getConceptMap(conceptData.getConceptReferenceTerm());
        org.openmrs.Concept mappedConcept = conceptMapper.map(conceptData, conceptClass, conceptDatatype, conceptAnswers, existingConcept);
        mappedConcept = conceptMapper.addConceptMap(mappedConcept, conceptMap);
        return mappedConcept;
    }

    private List<ConceptAnswer> getConceptAnswers(List<String> answers) {
        List<ConceptAnswer> conceptAnswers = new ArrayList<>();
        notFound = new ArrayList<>();
        if (answers == null) return conceptAnswers;
        for (String answer : answers) {
            org.openmrs.Concept answerConcept = conceptService.getConceptByName(answer);
            if (answerConcept == null) {
                notFound.add(answer);
            }
            conceptAnswers.add(constructConceptAnswer(answerConcept));
        }
        return conceptAnswers;
    }

    private List<org.openmrs.Concept> getSetMembers(List<String> children) {
        List<org.openmrs.Concept> setMembers = new ArrayList<>();
        notFound = new ArrayList<>();
        if (children == null) return setMembers;
        for (String child : children) {
            org.openmrs.Concept childConcept = conceptService.getConceptByName(child);
            if (childConcept == null) {
                notFound.add(child);
            }
            setMembers.add(childConcept);
        }
        return setMembers;
    }

    private ConceptAnswer constructConceptAnswer(org.openmrs.Concept answerConcept) {
        ConceptAnswer conceptAnswer = new ConceptAnswer(answerConcept);
        return conceptAnswer;
    }
}
