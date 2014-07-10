package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Person;

public class PatientData {
    private String uuid;

    public PatientData() {
    }

    public PatientData(Person person) {
        this.uuid = person.getUuid();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
