package org.bahmni.module.admin.observation;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.encounter.DuplicateObservationsMatcher;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObservationImportService {
    static final String FILE_IMPORT_COMMENT = "through file import";
    private ConceptService conceptService;

    public ObservationImportService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<EncounterTransaction.Observation> getObservations(EncounterRow encounterRow, Date visitStartDatetime, DuplicateObservationsMatcher duplicateObservationsMatcher) throws ParseException {
        boolean shouldMatchValue = false;
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        if (encounterRow.hasObservations()) {
            List<KeyValue> uniqueObsRows = duplicateObservationsMatcher.getUniqueObsRows(encounterRow.obsRows, shouldMatchValue);

            for (KeyValue obsRow : uniqueObsRows) {
                EncounterTransaction.Observation observation = createObservation(visitStartDatetime, obsRow);
                observations.add(observation);
            }
        }
        return observations;
    }

    private EncounterTransaction.Observation createObservation(Date visitStartDatetime, KeyValue obsRow) throws ParseException {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setConcept(getConcept(obsRow.getKey()));
        observation.setValue(obsRow.getValue());
        observation.setObservationDateTime(visitStartDatetime);
        observation.setComment(FILE_IMPORT_COMMENT);
        return observation;
    }

    private EncounterTransaction.Concept getConcept(String conceptName) {
        Concept obsConcept = conceptService.getConceptByName(conceptName);
        if(obsConcept == null)
            throw new ConceptNotFoundException("Concept '"+ conceptName +"' not found");
        return new EncounterTransaction.Concept(obsConcept.getUuid(), obsConcept.getName().getName());
    }

}
