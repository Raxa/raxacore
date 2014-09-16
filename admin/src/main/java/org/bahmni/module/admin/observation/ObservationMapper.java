package org.bahmni.module.admin.observation;

import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationMapper {
    private Map<String, Concept> cachedConcepts = new HashMap<>();

    private ConceptService conceptService;

    public ObservationMapper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<EncounterTransaction.Observation> getObservations(EncounterRow encounterRow) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        if (encounterRow.hasObservations()) {
            Date encounterDate = encounterRow.getEncounterDate();
            for (KeyValue obsRow : encounterRow.obsRows) {
                if (obsRow.getValue() != null && !StringUtils.isEmpty(obsRow.getValue().trim())) {
                    EncounterTransaction.Observation observation = createObservation(encounterDate, obsRow);
                    observations.add(observation);
                }
            }
        }
        return observations;
    }

    protected Concept getConcept(String conceptName) {
        if (!cachedConcepts.containsKey(conceptName)) {
            cachedConcepts.put(conceptName, fetchConcept(conceptName));
        }
        return cachedConcepts.get(conceptName);
    }

    private EncounterTransaction.Observation createObservation(Date encounterDate, KeyValue obsRow) throws ParseException {
        Concept obsConcept = getConcept(obsRow.getKey());
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept(obsConcept.getUuid(), obsConcept.getName().getName());

        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setConcept(concept);
        observation.setValue(getValue(obsRow, obsConcept));
        observation.setObservationDateTime(encounterDate);
        return observation;
    }

    private String getValue(KeyValue obsRow, Concept obsConcept) throws ParseException {
        if (obsConcept.getDatatype().isCoded()) {
            Concept valueConcept = conceptService.getConceptByName(obsRow.getValue());
            if (valueConcept == null)
                throw new ConceptNotFoundException(obsRow.getValue() + " not found");
            return valueConcept.getUuid();
        }
        return obsRow.getValue();
    }

    private Concept fetchConcept(String conceptName) {
        Concept obsConcept = conceptService.getConceptByName(conceptName);
        if (obsConcept == null)
            throw new ConceptNotFoundException("Concept '"+ conceptName +"' not found");

        return obsConcept;
    }

}
