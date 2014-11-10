package org.bahmni.module.referencedata.labconcepts.validator;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.referencedata.labconcepts.contract.Drug;
import org.bahmni.module.referencedata.labconcepts.model.DrugMetaData;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class DrugValidator extends Validator{

    private final DrugMetaDataValidator drugMetaDataValidator;

    public DrugValidator() {
        drugMetaDataValidator = new DrugMetaDataValidator();
    }

    public void validate(Drug drug, DrugMetaData drugMetaData) {
        drugMetaDataValidator.validate(drugMetaData);
        StringBuilder errors = new StringBuilder();
        if (StringUtils.isBlank(drug.getName())){
            errors.append("Drug name is mandatory\n");
        } if (StringUtils.isBlank(drug.getGenericName())){
            errors.append("Drug generic name is mandatory\n");
        }
        throwExceptionIfExists(errors);
    }
}
