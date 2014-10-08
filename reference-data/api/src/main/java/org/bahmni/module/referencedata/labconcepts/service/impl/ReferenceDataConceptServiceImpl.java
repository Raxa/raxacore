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
        HashSet<ConceptAnswer> conceptAnswers = getConceptAnswers(conceptData.getAnswers());
        validate(conceptData, conceptClass, conceptDatatype, conceptAnswers);
        ConceptMap conceptMap = mapToReferenceTerm(conceptData);
        org.openmrs.Concept existingConcept = conceptService.getConceptByName(conceptData.getUniqueName());
        org.openmrs.Concept mappedConcept = conceptMapper.map(conceptData, conceptClass, conceptDatatype, conceptAnswers, existingConcept);
        mappedConcept = addConceptMap(mappedConcept, conceptMap);
        return conceptService.saveConcept(mappedConcept);
    }

    @Override
    public org.openmrs.Concept saveConceptSet(ConceptSet conceptSet) {
        ConceptClass conceptClass = conceptService.getConceptClassByName(conceptSet.getClassName());
        org.openmrs.Concept existingConcept = conceptService.getConceptByName(conceptSet.getUniqueName());
        ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByUuid(ConceptDatatype.N_A_UUID);
        List<org.openmrs.Concept> setMembers = getSetMembers(conceptSet.getChildren());
        validate(conceptSet, conceptClass, setMembers);
        ConceptMap conceptMap = mapToReferenceTerm(conceptSet);
        org.openmrs.Concept mappedConceptSet = conceptSetMapper.map(conceptSet, setMembers, conceptClass, conceptDatatype, existingConcept);
        mappedConceptSet = addConceptMap(mappedConceptSet, conceptMap);
        return conceptService.saveConcept(mappedConceptSet);
    }

    private HashSet<ConceptAnswer> getConceptAnswers(List<String> answers) {
        HashSet<ConceptAnswer> conceptAnswers = new HashSet<>();
        if (answers == null) return conceptAnswers;
        for (String answer : answers) {
            org.openmrs.Concept answerConcept = conceptService.getConceptByName(answer);
            conceptAnswers.add(constructConceptAnswer(answerConcept));
        }
        return conceptAnswers;
    }

    private List<org.openmrs.Concept> getSetMembers(List<String> children) {
        List<org.openmrs.Concept> setMembers = new ArrayList<>();
        if (children == null) return setMembers;
        for (String child : children) {
            setMembers.add(conceptService.getConceptByName(child));
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

    private void validate(Concept conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype, HashSet<ConceptAnswer> conceptAnswers) {
        StringBuilder errors = new StringBuilder();
        if (conceptClassName == null) {
            errors.append("Concept Class " + conceptData.getClassName() + " not found\n");
        }
        if (conceptDatatype == null) {
            errors.append("Concept Datatype " + conceptData.getDataType() + " not found\n");
        } else if (!conceptDatatype.isCoded() && hasAnswers(conceptData)) {
            errors.append("Cannot create answers for concept " + conceptData.getUniqueName() + " having datatype " + conceptData.getDataType() + "\n");
        }
        for (org.openmrs.ConceptAnswer conceptAnswer : conceptAnswers) {
            if (conceptAnswer == null) {
                errors.append("Some Answer concepts do not exist\n");
            }
        }
        throwExceptionIfExists(errors);
    }

    private void validate(ConceptSet conceptSet, ConceptClass conceptClass, List<org.openmrs.Concept> setMembers) {
        StringBuilder errors = new StringBuilder();
        if (conceptClass == null) {
            errors.append("Concept Class " + conceptSet.getClassName() + " not found\n");
        }
        for (org.openmrs.Concept setMember : setMembers) {
            if (setMember == null) {
                errors.append("Some Child concepts do not exist\n");
            }
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
