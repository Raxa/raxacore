package org.bahmni.module.admin.csv.service;

import org.bahmni.csv.KeyValue;
import org.bahmni.form2.service.FormFieldPathService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.bahmni.module.admin.observation.CSVObservationHelper.getLastItem;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class FormFieldPathGeneratorService {

    private static final String FORM_NAMESPACE = "Bahmni";
    private static final String FORM_FIELD_PATH_SEPARATOR = "/";

    private FormFieldPathService formFieldPathService;

    @Autowired
    public FormFieldPathGeneratorService(FormFieldPathService formFieldPathService) {
        this.formFieldPathService = formFieldPathService;
    }

    public void setFormNamespaceAndFieldPath(List<EncounterTransaction.Observation> form2Observations, List<String> form2CSVHeaderParts) {
        if (isEmpty(form2Observations)) {
            return;
        }
        final EncounterTransaction.Observation observation = getLastItem(form2Observations);
        final String formFieldPath = formFieldPathService.getFormFieldPath(form2CSVHeaderParts);
        observation.setFormFieldPath(formFieldPath);
        observation.setFormNamespace(FORM_NAMESPACE);
    }

    public void setFormNamespaceAndFieldPathForMultiSelectObs(List<EncounterTransaction.Observation> form2Observations, List<String> form2CSVHeaderParts, List<KeyValue> multiSelectForm2CSVObservations) {
        if (isEmpty(form2Observations)) {
            return;
        }
        int prevObsCount = form2Observations.size() - multiSelectForm2CSVObservations.size();
        for(int i = 0; i < multiSelectForm2CSVObservations.size(); i++) {
            final EncounterTransaction.Observation observation = form2Observations.get(prevObsCount + i);
            final String formFieldPath = formFieldPathService.getFormFieldPath(form2CSVHeaderParts);
            observation.setFormFieldPath(formFieldPath);
            observation.setFormNamespace(FORM_NAMESPACE);
        }
    }

    public void setFormNamespaceAndFieldPathForAddmoreObs(List<EncounterTransaction.Observation> form2Observations, List<String> form2CSVHeaderParts, List<KeyValue> addmoreForm2CSVObservations) {
        if (isEmpty(form2Observations)) {
            return;
        }
        int prevObsCount = form2Observations.size() - addmoreForm2CSVObservations.size();
        final String formFieldPath = formFieldPathService.getFormFieldPath(form2CSVHeaderParts);
        String[] tokens = formFieldPath.split(FORM_FIELD_PATH_SEPARATOR);
        int formFieldPathPosition = tokens.length - 1;
        String path = tokens[formFieldPathPosition];
        String controlIdPrefix = path.split("-")[0];

        for(int i = 0; i < addmoreForm2CSVObservations.size(); i++) {
            final EncounterTransaction.Observation observation = form2Observations.get(prevObsCount + i);
            tokens[formFieldPathPosition] = controlIdPrefix + "-" + i;
            observation.setFormFieldPath(String.join(FORM_FIELD_PATH_SEPARATOR, tokens));
            observation.setFormNamespace(FORM_NAMESPACE);
        }
    }
}
