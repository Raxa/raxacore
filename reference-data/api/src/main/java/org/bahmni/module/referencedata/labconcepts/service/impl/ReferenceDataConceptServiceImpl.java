package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptMapper;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptSetMapper;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptReferenceTermService;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class ReferenceDataConceptServiceImpl implements ReferenceDataConceptService {
    private ConceptService conceptService;
    private ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService;
    private ConceptMapper conceptMapper;
    private ConceptSetMapper conceptSetMapper;

    @Autowired
    public ReferenceDataConceptServiceImpl(ConceptService conceptService, ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService) {
        this.conceptMapper = new ConceptMapper();
        this.conceptSetMapper = new ConceptSetMapper();
        this.conceptService = conceptService;
        this.referenceDataConceptReferenceTermService = referenceDataConceptReferenceTermService;
    }

    @Override
    public org.openmrs.Concept saveConcept(Concept conceptData) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptData.getClassName());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptData.getDataType());
        validate(conceptData, conceptClass, conceptDatatype);
        if (conceptDatatype.isCoded()) {
            return saveCodedConcept(conceptData, conceptDatatype, conceptClass);
        }
        return saveConcept(conceptData, conceptDatatype, conceptClass, new HashSet<ConceptAnswer>());
    }

    @Override
    public org.openmrs.Concept saveConceptSet(ConceptSet conceptSet) {
        org.openmrs.Concept mappedConcept = conceptSetMapper.map(conceptSet, null, new ArrayList<org.openmrs.Concept>());
        return conceptService.saveConcept(mappedConcept);

    }

    private org.openmrs.Concept saveCodedConcept(Concept conceptData, ConceptDatatype conceptDatatype, ConceptClass conceptClassName) {
        HashSet<ConceptAnswer> answers = new HashSet<>();
        if (hasAnswers(conceptData)) {
            answers = constructAnswers(conceptData);
        }
        return saveConcept(conceptData, conceptDatatype, conceptClassName, answers);
    }

    private org.openmrs.Concept saveConcept(Concept conceptData, ConceptDatatype conceptDatatype, ConceptClass conceptClassName, HashSet<ConceptAnswer> answers) {
        ConceptMap conceptMap = mapToReferenceTerm(conceptData);
        org.openmrs.Concept existingConcept = conceptService.getConceptByName(conceptData.getUniqueName());
        org.openmrs.Concept mappedConcept = conceptMapper.map(conceptData, conceptClassName, conceptDatatype, answers, existingConcept);
        mappedConcept = addConceptMap(mappedConcept, conceptMap);
        return conceptService.saveConcept(mappedConcept);
    }

    private org.openmrs.Concept addConceptMap(org.openmrs.Concept mappedConcept, ConceptMap conceptMap) {
        if (conceptMap == null) return mappedConcept;
        for (ConceptMap existingMap : mappedConcept.getConceptMappings()) {
            if (existingMap.getConceptReferenceTerm().equals(conceptMap.getConceptReferenceTerm())) {
                return mappedConcept;
            }
        }
        mappedConcept.addConceptMapping(conceptMap);
        return mappedConcept;
    }

    private ConceptMap mapToReferenceTerm(Concept conceptData) {
        ConceptMap conceptMap = null;
        if (conceptData.getConceptReferenceTerm() != null && hasReferenceTermAndSource(conceptData)) {
            ConceptReferenceTerm conceptReferenceTerm = referenceDataConceptReferenceTermService.getConceptReferenceTerm(conceptData.getConceptReferenceTerm().getReferenceTermCode(), conceptData.getConceptReferenceTerm().getReferenceTermSource());
            String mapType = conceptData.getConceptReferenceTerm().getReferenceTermRelationship();
            ConceptMapType conceptMapType = conceptService.getConceptMapTypeByName(mapType);
            if (conceptMapType == null) {
                conceptMapType = conceptService.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
            }
            conceptMap = new ConceptMap(conceptReferenceTerm, conceptMapType);
        }
        return conceptMap;

    }

    private boolean hasReferenceTermAndSource(Concept conceptData) {
        return !(StringUtils.isEmpty(conceptData.getConceptReferenceTerm().getReferenceTermCode()) || StringUtils.isEmpty(conceptData.getConceptReferenceTerm().getReferenceTermSource()));
    }

    private HashSet<ConceptAnswer> constructAnswers(Concept conceptData) {
        HashSet<ConceptAnswer> answersConcept = new HashSet<>();
        double sortWeight = 1;
        StringBuilder errors = new StringBuilder();

        for (String answer : conceptData.getAnswers()) {
            org.openmrs.Concept answerConcept = conceptService.getConcept(answer);
            if (answerConcept == null) {
                errors.append("Answer Concept " + answer + " not found\n");
            } else {
                answersConcept.add(constructConceptAnswer(answerConcept, sortWeight));
            }
            sortWeight++;
        }

        throwExceptionIfExists(errors);
        return answersConcept;
    }

    private ConceptAnswer constructConceptAnswer(org.openmrs.Concept answerConcept, double sortWeight) {
        ConceptAnswer conceptAnswer = new ConceptAnswer(answerConcept);
        conceptAnswer.setSortWeight(sortWeight);
        return conceptAnswer;
    }

    private void validate(Concept conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype) {
        StringBuilder errors = new StringBuilder();
        if (conceptClassName == null) {
            errors.append("Concept Class " + conceptData.getClassName() + " not found\n");
        }
        if (conceptDatatype == null) {
            errors.append("Concept Datatype " + conceptData.getDataType() + " not found\n");
        } else if (!conceptDatatype.isCoded() && hasAnswers(conceptData)) {
            errors.append("Cannot create answers for concept " + conceptData.getUniqueName() + " having datatype " + conceptData.getDataType() + "\n");
        }
        throwExceptionIfExists(errors);
    }

    private boolean hasAnswers(Concept conceptData) {
        return conceptData.getAnswers() != null && conceptData.getAnswers().size() > 0;
    }

    private void throwExceptionIfExists(StringBuilder errors) {
        String message = errors.toString();
        if (!StringUtils.isBlank(message)) {
            throw new APIException(message);
        }
    }

}
