package org.bahmni.module.bahmnicore.contract.encounter.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterData {
    private String encounterUUID;
    private Date encounterDate;
    private List<ObservationData> observations = new ArrayList<>();
    public EncounterData(String encounterUUID, Date encounterDate) {

        this.encounterUUID = encounterUUID;
        this.encounterDate = encounterDate;
    }

    public EncounterData() {
    }

    public String getEncounterUUID() {
        return encounterUUID;
    }

    public void setEncounterUUID(String encounterUUID) {
        this.encounterUUID = encounterUUID;
    }

    public Date getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Date encounterDate) {
        this.encounterDate = encounterDate;
    }

    public void addObservationData(ObservationData observationData){
        observations.add(observationData);
    }
}