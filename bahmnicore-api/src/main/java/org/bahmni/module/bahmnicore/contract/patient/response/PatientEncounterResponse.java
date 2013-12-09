package org.bahmni.module.bahmnicore.contract.patient.response;

import org.bahmni.module.bahmnicore.contract.encounter.data.EncounterData;
import org.bahmni.module.bahmnicore.contract.encounter.data.ObservationData;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PatientEncounterResponse {
    private List<EncounterData> encounterList = new ArrayList<>();


    public PatientEncounterResponse(List<Encounter> encounters) {
        filterNumericObsOnly(encounters);
    }

    private void filterNumericObsOnly(List<Encounter> encounters) {
        for(Encounter encounter : encounters) {
            EncounterData encounterData = new EncounterData();
            encounterData.setEncounterDate(encounter.getEncounterDatetime());
            Set<Obs> allObs = encounter.getAllObs();
            for (Obs obs : allObs) {
                Concept concept = obs.getConcept();
                ConceptDatatype datatype = concept.getDatatype();
                Object value = datatype.isNumeric() ? obs.getValueNumeric() : obs.getValueAsString(Locale.getDefault());
                encounterData.addObservationData(new ObservationData(concept.getUuid(), concept.getName().getName(), value));
            }
            encounterList.add(encounterData);
        }
    }

    public PatientEncounterResponse() {
    }


}