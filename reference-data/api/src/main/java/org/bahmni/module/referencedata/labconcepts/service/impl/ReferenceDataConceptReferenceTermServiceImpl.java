package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptReferenceTermService;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferenceDataConceptReferenceTermServiceImpl implements ReferenceDataConceptReferenceTermService {

    @Autowired
    private ConceptService conceptService;

    @Override
    public ConceptReferenceTerm getConceptReferenceTerm(String referenceTermCode, String referenceTermSource) {
        ConceptSource conceptReferenceSource = conceptService.getConceptSourceByName(referenceTermSource);
        ConceptReferenceTerm conceptReferenceTerm = conceptService.getConceptReferenceTermByCode(referenceTermCode, conceptReferenceSource);
        validate(conceptReferenceSource, conceptReferenceTerm);
        return conceptReferenceTerm;
    }

    private void validate(ConceptSource referenceTermSource, ConceptReferenceTerm referenceTerm) {
        StringBuilder errors = new StringBuilder();
        if (referenceTermSource == null) {
            errors.append("Concept reference source not found\n");
        }
        if (referenceTerm == null) {
            errors.append("Concept reference term code not found\n");
        } else if (!referenceTerm.getConceptSource().equals(referenceTermSource)) {
            errors.append("Concept reference term not mapped to the given source\n");
        }
        throwExceptionIfExists(errors);
    }

    private void throwExceptionIfExists(StringBuilder errors) {
        String message = errors.toString();
        if (!StringUtils.isBlank(message)) {
            throw new APIException(message);
        }
    }
}
