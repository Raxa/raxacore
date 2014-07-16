package org.openmrs.module.bahmnicore.web.v1_0.mapper;


import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;

import java.util.*;

public class BahmniObservationsMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    private static final String DURATION_CONCEPT_CLASS = "Duration";

    public static final String PATIENT_RESORUCE_NAME = RestConstants.VERSION_1 + "/patient";
    public static final String ENCOUNTER_RESORUCE_NAME = RestConstants.VERSION_1 + "/encounter";
    public static final String VISIT_RESORUCE_NAME = RestConstants.VERSION_1 + "/visit";
    public static final long INVALID_DEFAULT_DURATION = -1l;

    private final RestService restService;

    public BahmniObservationsMapper(RestService restService) {
        this.restService = restService;
    }

    public List<ObservationData> map(List<Obs> obsForPerson) {
        return recurse(new HashSet<>(obsForPerson), new ArrayList<ObservationData>());
    }

    private List<ObservationData> recurse(Set<Obs> obsForPerson, List<ObservationData> mappedObservations) {
        for (Obs obs : obsForPerson) {
            Set<Obs> groupMembers = obs.getGroupMembers();
            if (groupMembers == null || groupMembers.isEmpty()) {
                mappedObservations.add(createObservationForLeaf(obs));
            } else if (isConceptDetails(obs.getConcept())) {
                mappedObservations.add(createObservationForGroup(obs));
            } else {
                recurse(groupMembers, mappedObservations);
            }
        }

        return mappedObservations;
    }

    private ObservationData createObservationForGroup(Obs conceptDetailsObs) {
        ObservationData observationData = null;
        Long duration = null;
        boolean isAbnormal = false;
        for (Obs anObservation : conceptDetailsObs.getGroupMembers()) {
            if (isDuration(anObservation.getConcept())) {
                duration = anObservation.getValueNumeric().longValue();
            } else if (isAbnormal(anObservation.getConcept())) {
                isAbnormal = Boolean.parseBoolean(anObservation.getValueCoded().getName().getName());
            } else if (hasValue(anObservation)) {
                observationData = createObservationForLeaf(anObservation);
            }
        }

        observationData.setDuration(duration);
        observationData.setIsAbnormal(isAbnormal);
        return observationData;
    }

    private ObservationData createObservationForLeaf(Obs anObservation) {
        return new ObservationData(anObservation, getPatientURI(anObservation), getVisitURI(anObservation), getEncounterURI(anObservation));
    }

    private String getPatientURI(Obs anObservation) {
        return getURI(PATIENT_RESORUCE_NAME, new Patient(anObservation.getPerson()));
    }

    private String getVisitURI(Obs anObservation) {
        return getURI(VISIT_RESORUCE_NAME, anObservation.getEncounter().getVisit());
    }

    private String getEncounterURI(Obs anObservation) {
        return getURI(ENCOUNTER_RESORUCE_NAME, anObservation.getEncounter());
    }

    private String getURI(String resourceName, Object resourceInstance) {
        return restService.getResourceByName(resourceName).getUri(resourceInstance);
    }

    private boolean hasValue(Obs anObservation) {
        return StringUtils.isNotBlank(anObservation.getValueAsString(Context.getLocale()));
    }

    private boolean isAbnormal(Concept obsConcept) {
        return obsConcept.getConceptClass().getName().equals(ABNORMAL_CONCEPT_CLASS);
    }

    private boolean isDuration(Concept obsConcept) {
        return obsConcept.getConceptClass().getName().equals(DURATION_CONCEPT_CLASS);
    }

    private boolean isConceptDetails(Concept obsConcept) {
        return obsConcept.getConceptClass().getName().equals(CONCEPT_DETAILS_CONCEPT_CLASS);
    }

}