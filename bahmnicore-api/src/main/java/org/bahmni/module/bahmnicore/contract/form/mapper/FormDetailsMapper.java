package org.bahmni.module.bahmnicore.contract.form.mapper;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.contract.form.helper.FormType;
import org.bahmni.module.bahmnicore.contract.form.helper.FormUtil;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.Visit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FormDetailsMapper {

    public static Collection<FormDetails> createFormDetails(List<Obs> observations, FormType formType) {
        HashMap<FormDetails, FormDetails> formDetailsMap = new HashMap<>();
        observations.forEach(obs -> {
            FormDetails formDetails = FormDetailsMapper.map(obs, formType);
            addMultipleProvidersOfAForm(formDetailsMap, formDetails);
        });
        return formDetailsMap.keySet();
    }

    private static FormDetails map(Obs obs, FormType formType) {

        Encounter encounter = obs.getEncounter();
        Visit visit = encounter.getVisit();
        User creator = obs.getCreator();

        FormDetails formDetails = new FormDetails();

        formDetails.setFormType(formType.get());
        if (formType.equals(FormType.FORM_BUILDER_FORMS)) {
            formDetails.setFormName(FormUtil.getFormNameFromFieldPath(obs.getFormFieldPath()));
            formDetails.setFormVersion(FormUtil.getFormVersionFromFieldPath(obs.getFormFieldPath()));
        } else if (formType.equals(FormType.ALL_OBSERVATION_TEMPLATE_FORMS)) {
            formDetails.setFormName(obs.getConcept().getName().getName());
        }
        formDetails.setEncounterUuid(encounter.getUuid());
        formDetails.setEncounterDateTime(encounter.getEncounterDatetime());
        formDetails.setVisitUuid(visit.getUuid());
        formDetails.setVisitStartDateTime(visit.getStartDatetime());
        formDetails.addProvider(creator.getPersonName().getFullName(), creator.getUuid());
        return formDetails;
    }

    private static void addMultipleProvidersOfAForm(HashMap<FormDetails, FormDetails> formDetailsMap, FormDetails formDetails) {
        if (formDetailsMap.containsKey(formDetails)) {
            formDetails.getProviders().forEach(provider ->
                    formDetailsMap.get(formDetails).addProvider(provider.getProviderName(), provider.getUuid()));
        } else {
            formDetailsMap.put(formDetails, formDetails);
        }
    }

}
