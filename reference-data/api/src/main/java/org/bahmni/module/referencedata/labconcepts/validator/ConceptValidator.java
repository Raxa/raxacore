package org.bahmni.module.referencedata.labconcepts.validator;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptCommon;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.APIException;

import java.util.List;

public class ConceptValidator {

    public void validate(Concept conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype, List<String> notFound) {
        StringBuilder errors = validateConceptCommon(conceptData, conceptClassName, conceptDatatype, notFound);
        if (conceptDatatype != null && !conceptDatatype.isCoded() && hasAnswers(conceptData)) {
            errors.append("Cannot create answers for concept " + conceptData.getUniqueName() + " having datatype " + conceptData.getDataType() + "\n");
        }
        throwExceptionIfExists(errors);
    }

    public void validate(ConceptSet conceptSet, ConceptClass conceptClass, ConceptDatatype conceptDatatype, List<String> notFound) {
        StringBuilder errors = validateConceptCommon(conceptSet, conceptClass, conceptDatatype, notFound);
        throwExceptionIfExists(errors);
    }

    private StringBuilder validateConceptCommon(ConceptCommon conceptData, ConceptClass conceptClassName, ConceptDatatype conceptDatatype, List<String> notFound) {
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

    private boolean hasAnswers(Concept conceptData) {
        return conceptData.getAnswers() != null && conceptData.getAnswers().size() > 0;
    }
}
