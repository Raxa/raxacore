package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.contract.form.helper.FormType;
import org.bahmni.module.bahmnicore.contract.form.helper.ObsUtil;
import org.bahmni.module.bahmnicore.service.BahmniFormDetailsService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.bahmni.module.bahmnicore.contract.form.mapper.FormDetailsMapper.createFormDetails;

@Service
public class BahmniFormDetailsServiceImpl implements BahmniFormDetailsService {

    private final VisitService visitService;
    private final PatientService patientService;
    private final EncounterService encounterService;
    private final ObsService obsService;

    @Autowired
    public BahmniFormDetailsServiceImpl(PatientService patientService, VisitService visitService,
                                        EncounterService encounterService, ObsService obsService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.encounterService = encounterService;
        this.obsService = obsService;
    }

    @Override
    public Collection<FormDetails> getFormDetails(String patientUuid, String formType, int numberOfVisits) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        if (patient == null) {
            return Collections.emptyList();
        }
        List<Visit> visits = visitService.getVisitsByPatient(patient);
        List<Visit> limitedVisits = limitVisits(visits, numberOfVisits);

        EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder()/*.setPatient(patient)*/
                .setVisits(limitedVisits).createEncounterSearchCriteria();
        List<Encounter> encounters = encounterService.getEncounters(encounterSearchCriteria);

        List<Obs> observations = new ArrayList<>();
        if (isNotEmpty(encounters) && isNotEmpty(limitedVisits)) {
            observations = obsService.getObservations(Collections.singletonList(patient.getPerson()), encounters,
                    null, null, null, null, null, null, null, null, null, false);
        }

        Collection<FormDetails> formDetails = new ArrayList<>();

        if (FormType.FORM_BUILDER_FORMS.get().equals(formType) || StringUtils.isBlank(formType)) {
            formDetails = createFormDetails(ObsUtil.filterFormBuilderObs(observations), FormType.FORM_BUILDER_FORMS);
        }
        return formDetails;
    }

    private List<Visit> limitVisits(List<Visit> visits, int numberOfVisits) {
        if (numberOfVisits <= -1) {
            return visits;
        }
        return visits.size() > numberOfVisits ? visits.subList(0, numberOfVisits) : visits;
    }


}
