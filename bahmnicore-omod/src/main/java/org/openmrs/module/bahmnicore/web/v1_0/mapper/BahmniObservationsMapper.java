package org.openmrs.module.bahmnicore.web.v1_0.mapper;


import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.utils.HibernateLazyLoader;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;

import java.util.*;

public class BahmniObservationsMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    private static final String DURATION_CONCEPT_CLASS = "Duration";

    public static final String PATIENT_RESOURCE_NAME = RestConstants.VERSION_1 + "/patient";
    public static final String ENCOUNTER_RESOURCE_NAME = RestConstants.VERSION_1 + "/encounter";
    public static final String VISIT_RESOURCE_NAME = RestConstants.VERSION_1 + "/visit";
    private static final String PROVIDER_RESOURCE_NAME = RestConstants.VERSION_1 + "/provider";

    private final RestService restService;
    private final List<String> rootConceptNames;

    public BahmniObservationsMapper(RestService restService, String[] conceptNames) {
        this.restService = restService;
        this.rootConceptNames = Arrays.asList(conceptNames);
    }

    public List<ObservationData> map(List<Obs> obsForPerson) {
        List<ObservationData> observations = flatten(obsForPerson, new ArrayList<ObservationData>(), null);

        sortByDatetime(observations);
        return observations;
    }

    private void sortByDatetime(List<ObservationData> observations) {
        Collections.sort(observations, new Comparator<ObservationData>() {
            @Override
            public int compare(ObservationData anObs, ObservationData anotherObs) {
                return anotherObs.getTime().compareTo(anObs.getTime());
            }
        });
    }

    private List<ObservationData> flatten(Collection<Obs> obsForPerson, List<ObservationData> mappedObservations, String rootConcept) {
        for (Obs obs : obsForPerson) {
            rootConcept = getRootConcept(obs, rootConcept);

            Collection<Obs> groupMembers = obs.getGroupMembers();
            if (groupMembers == null || groupMembers.isEmpty()) {
                mappedObservations.add(createObservationForLeaf(obs, rootConcept));
            } else if (isConceptDetails(obs.getConcept())) {
                mappedObservations.add(createObservationForGroup(obs, rootConcept));
            } else {
                flatten(groupMembers, mappedObservations, rootConcept);
            }
        }

        return mappedObservations;
    }

    private ObservationData createObservationForGroup(Obs conceptDetailsObs, String rootConcept) {
        ObservationData observationData = null;
        Long duration = null;
        boolean isAbnormal = false;
        for (Obs anObservation : conceptDetailsObs.getGroupMembers()) {
            if (isDuration(anObservation.getConcept())) {
                duration = anObservation.getValueNumeric().longValue();
            } else if (isAbnormal(anObservation.getConcept())) {
                isAbnormal = Boolean.parseBoolean(anObservation.getValueCoded().getName().getName());
            } else if (hasValue(anObservation)) {
                observationData = createObservationForLeaf(anObservation, rootConcept);
                observationData.setRootConcept(rootConcept);
                // Mujir/Mihir - not pre loading complex concepts as we don't need them yet.
                if (isNumeric(anObservation)) {
                    observationData.setUnit(getUnit(anObservation.getConcept()));
                }
            }
        }

        observationData.setDuration(duration);
        observationData.setIsAbnormal(isAbnormal);
        return observationData;
    }

    private String getUnit(Concept concept) {
        ConceptNumeric conceptNumeric = (ConceptNumeric) new HibernateLazyLoader().load(concept);
        return conceptNumeric.getUnits();
    }

    private boolean isNumeric(Obs anObservation) {
        return anObservation.getConcept().getDatatype().getHl7Abbreviation().equals(ConceptDatatype.NUMERIC);
    }

    private ObservationData createObservationForLeaf(Obs anObservation, String rootConcept) {
        ObservationData observationData = new ObservationData(anObservation, getPatientURI(anObservation), getVisitURI(anObservation), getEncounterURI(anObservation), getProviderURIs(anObservation));
        observationData.setRootConcept(rootConcept);
        return observationData;
    }

    private List<String> getProviderURIs(Obs anObservation) {
        List<String> providerURIs = new ArrayList<>();
        for (EncounterProvider encounterProvider : anObservation.getEncounter().getEncounterProviders()) {
            providerURIs.add(getURI(PROVIDER_RESOURCE_NAME, encounterProvider.getProvider()));
        }
        return providerURIs;
    }

    private String getPatientURI(Obs anObservation) {
        return getURI(PATIENT_RESOURCE_NAME, new Patient(anObservation.getPerson()));
    }

    private String getVisitURI(Obs anObservation) {
        return getURI(VISIT_RESOURCE_NAME, anObservation.getEncounter().getVisit());
    }

    private String getEncounterURI(Obs anObservation) {
        return getURI(ENCOUNTER_RESOURCE_NAME, anObservation.getEncounter());
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

    public String getRootConcept(Obs obs, String rootConcept) {
        String conceptName = obs.getConcept().getName().getName();
        return rootConceptNames.contains(conceptName) ? conceptName : rootConcept;
    }
}