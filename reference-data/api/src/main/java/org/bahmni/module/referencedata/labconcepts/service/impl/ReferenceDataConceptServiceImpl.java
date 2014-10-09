package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptCommon;
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
import java.util.List;

@Service
public class ReferenceDataConceptServiceImpl implements ReferenceDataConceptService {
    private ConceptService conceptService;
    private ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService;
    private ConceptMapper conceptMapper;
    private ConceptSetMapper conceptSetMapper;
    private List<String> notFound;

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
        org.openmrs.Concept existingConcept = conceptService.getConceptByName(conceptData.getUniqueName());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptData.getDataType());
        org.openmrs.Concept mappedConcept = getConcept(conceptData, conceptClass, conceptDatatype, existingConcept);
        return conceptService.saveConcept(mappedConcept);
    }

    @Override
    public org.openmrs.Concept saveConcept(ConceptSet conceptSet) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptSet.getClassName());
        org.openmrs.Concept existingConcept = conceptService.getConceptByName(conceptSet.getUniqueName());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptSet.getDataType());
        org.openmrs.Concept mappedConceptSet = getConceptSet(conceptSet, conceptClass, existingConcept, conceptDatatype);
        return conceptService.saveConcept(mappedConceptSet);
    }

    private org.openmrs.Concept getConceptSet(ConceptSet conceptSet, ConceptClass conceptClass, org.openmrs.Concept existingConcept, ConceptDatatype conceptDatatype) {
        List<org.openmrs.Concept> setMembers = getSetMembers(conceptSet.getChildren());
        validate(conceptSet, conceptClass, conceptDatatype);
        ConceptMap conceptMap = mapToReferenceTerm(conceptSet);
        org.openmrs.Concept mappedConceptSet = conceptSetMapper.map(conceptSet, setMembers, conceptClass, conceptDatatype, existingConcept);
        mappedConceptSet = addConceptMap(mappedConceptSet, conceptMap);
        return mappedConceptSet;
    }

    private org.openmrs.Concept getConcept(Concept conceptData, ConceptClass conceptClass, ConceptDatatype conceptDatatype, org.openmrs.Concept existingConcept) {
        List<ConceptAnswer> conceptAnswers = getConceptAnswers(conceptData.getAnswers());
        validate(conceptData, conceptClass, conceptDatatype);
        ConceptMap conceptMap = mapToReferenceTerm(conceptData);
        org.openmrs.Concept mappedConcept = conceptMapper.map(conceptData, conceptClass, conceptDatatype, conceptAnswers, existingConcept);
        mappedConcept = addConceptMap(mappedConcept, conceptMap);
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

    private ConceptMap mapToReferenceTerm(ConceptCommon conceptCommon) {
        ConceptMap conceptMap = null;
        if (conceptCommon.getConceptReferenceTerm() != null && hasReferenceTermAndSource(conceptCommon)) {
            ConceptReferenceTerm conceptReferenceTerm = referenceDataConceptReferenceTermService.getConceptReferenceTerm(conceptCommon.getConceptReferenceTerm().getReferenceTermCode(), conceptCommon.getConceptReferenceTerm().getReferenceTermSource());
            String mapType = conceptCommon.getConceptReferenceTerm().getReferenceTermRelationship();
            ConceptMapType conceptMapType = conceptService.getConceptMapTypeByName(mapType);
            if (conceptMapType == null) {
                conceptMapType = conceptService.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
            }
            conceptMap = new ConceptMap(conceptReferenceTerm, conceptMapType);
        }
        return conceptMap;

    }

    private boolean hasReferenceTermAndSource(ConceptCommon conceptCommon) {
        return !(StringUtils.isEmpty(conceptCommon.getConceptReferenceTerm().getReferenceTermCode()) || StringUtils.isEmpty(conceptCommon.getConceptReferenceTerm().getReferenceTermSource()));
    }

    private ConceptAnswer constructConceptAnswer(org.openmrs.Concept answerConcept) {
        ConceptAnswer conceptAnswer = new ConceptAnswer(answerConcept);
        return conceptAnswer;
    }

    private void validate(Concept conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype) {
        StringBuilder errors = validateConceptCommon(conceptData, conceptClassName, conceptDatatype);
        if (conceptDatatype != null && !conceptDatatype.isCoded() && hasAnswers(conceptData)) {
            errors.append("Cannot create answers for concept " + conceptData.getUniqueName() + " having datatype " + conceptData.getDataType() + "\n");
        }
        throwExceptionIfExists(errors);
    }

    private void validate(ConceptSet conceptSet, ConceptClass conceptClass, ConceptDatatype conceptDatatype) {
        StringBuilder errors = validateConceptCommon(conceptSet, conceptClass, conceptDatatype);
        throwExceptionIfExists(errors);
    }

    private boolean hasAnswers(Concept conceptData) {
        return conceptData.getAnswers() != null && conceptData.getAnswers().size() > 0;
    }

    private StringBuilder validateConceptCommon(ConceptCommon conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype) {
        StringBuilder errors = new StringBuilder();
        if (conceptClassName == null) {
            errors.append("Concept Class " + conceptData.getClassName() + " not found\n");
        }
        if (conceptDatatype == null) {
            errors.append("Concept Datatype " + conceptData.getDataType() + " not found\n");
        }
        for (String notFoundItem : notFound) {
            errors.append(notFoundItem + " Concept/ConceptAnswer not found\n");
        }
        return errors;
    }

    private void throwExceptionIfExists(StringBuilder errors) {
        String message = errors.toString();
        if (!StringUtils.isBlank(message)) {
            throw new APIException(message);
        }
    }

}
