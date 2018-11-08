package org.bahmni.module.bahmnicore.contract.form.mapper;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.contract.form.helper.FormType;
import org.bahmni.module.bahmnicore.contract.form.helper.FormUtil;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.Visit;

public class FormDetailsMapper {

    public static FormDetails map(Obs obs, FormType formType) {

        Encounter encounter = obs.getEncounter();
        Visit visit = encounter.getVisit();
        User creator = obs.getCreator();

        FormDetails formDetails = new FormDetails();

        formDetails.setFormType(formType.get());
        if (formType.equals(FormType.FORM_BUILDER_FORMS)) {
            formDetails.setFormName(FormUtil.getFormNameFromFieldPath(obs.getFormFieldPath()));
            formDetails.setFormVersion(FormUtil.getFormVersionFromFieldPath(obs.getFormFieldPath()));
        }
        formDetails.setEncounterUuid(encounter.getUuid());
        formDetails.setEncounterDateTime(encounter.getEncounterDatetime());
        formDetails.setVisitUuid(visit.getUuid());
        formDetails.setVisitStartDateTime(visit.getStartDatetime());
        formDetails.addProvider(creator.getPersonName().getFullName(), creator.getUuid());
        return formDetails;
    }

}
