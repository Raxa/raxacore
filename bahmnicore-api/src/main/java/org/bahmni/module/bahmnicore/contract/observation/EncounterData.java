package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Encounter;

import java.util.Date;

public class EncounterData {
    private String uuid;
    private Date encounterDateTime;

    public EncounterData() {
    }

    public EncounterData(Encounter encounter) {
        this.uuid = encounter.getUuid();
        this.encounterDateTime = encounter.getEncounterDatetime();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getEncounterDateTime() {
        return encounterDateTime;
    }

    public void setEncounterDateTime(Date encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }
}
