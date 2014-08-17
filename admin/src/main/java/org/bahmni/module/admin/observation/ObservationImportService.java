package org.bahmni.module.admin.observation;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.encounter.DuplicateObservationsMatcher;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ObservationImportService {
    static final String FILE_IMPORT_COMMENT = "through file import";
    private ConceptService conceptService;

    public ObservationImportService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<EncounterTransaction.Observation> getObservations(EncounterRow encounterRow,
                             DuplicateObservationsMatcher duplicateObservationsMatcher) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        if (encounterRow.obsRows != null) {
            List<KeyValue> matchingObservations = duplicateObservationsMatcher.matchingObservations(encounterRow.obsRows);

            List<KeyValue> obsRows = encounterRow.obsRows;
            for (KeyValue obsRow : obsRows) {
                if (shouldIgnoreObservation(matchingObservations, obsRow)) {
                    continue;
                }
                EncounterTransaction.Observation observation = createObservation(encounterRow, obsRow);
                observations.add(observation);
            }
        }
        return observations;
    }

    private EncounterTransaction.Observation createObservation(EncounterRow encounterRow, KeyValue obsRow) throws ParseException {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setConcept(getConcept(obsRow.getKey()));
        observation.setValue(obsRow.getValue());
        observation.setObservationDateTime(encounterRow.getEncounterDate());
        observation.setComment(FILE_IMPORT_COMMENT);
        return observation;
    }

    private boolean shouldIgnoreObservation(List<KeyValue> matchingObservations, KeyValue anObsRow) {
        return matchingObservations.contains(anObsRow);
    }

    private EncounterTransaction.Concept getConcept(String conceptName) {
        Concept obsConcept = conceptService.getConceptByName(conceptName);
        return new EncounterTransaction.Concept(obsConcept.getUuid());
    }

}
