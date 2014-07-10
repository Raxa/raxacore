package org.openmrs.module.bahmnicore.web.v1_0.mapper;


import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.util.LocaleUtility;

import java.util.*;

public class BahmniObservationsMapper {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    private static final String DURATION_CONCEPT_CLASS = "Duration";

    public List<ObservationData> map(List<Obs> obsForPerson) {
        return recurse(new HashSet<>(obsForPerson), new ArrayList<ObservationData>());
    }

    private List<ObservationData> recurse(Set<Obs> obsForPerson, List<ObservationData> mappedObservations) {
        for (Obs obs : obsForPerson) {
            Set<Obs> groupMembers = obs.getGroupMembers(); // TODO : null condition

            if (groupMembers == null || groupMembers.isEmpty()) {
                mappedObservations.add(new ObservationData(obs));
            } else if (isConceptDetails(obs.getConcept())) {
                mappedObservations.add(mapFruit(obs));
            } else {
                recurse(groupMembers, mappedObservations);
            }
        }

        return mappedObservations;
    }

    private ObservationData mapFruit(Obs conceptDetailsObs) {
        ObservationData observationData = null;
        long duration = 0l;
        boolean isAbnormal = false;
        for (Obs anObservation : conceptDetailsObs.getGroupMembers()) {
            if (isDuration(anObservation.getConcept())) {
                duration = anObservation.getValueNumeric().longValue();
            } else if (isAbnormal(anObservation.getConcept())) {
                isAbnormal = Boolean.parseBoolean(anObservation.getValueCoded().getName().getName());
            } else if (hasValue(anObservation)) {
                observationData = new ObservationData(anObservation);
            }
        }

        observationData.setDuration(duration);
        observationData.setAbnormal(isAbnormal);
        return observationData;
    }

    private boolean hasValue(Obs anObservation) {
        return StringUtils.isNotBlank(anObservation.getValueAsString(LocaleUtility.getDefaultLocale()));
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