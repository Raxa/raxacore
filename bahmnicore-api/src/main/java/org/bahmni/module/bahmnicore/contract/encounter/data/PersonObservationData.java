package org.bahmni.module.bahmnicore.contract.encounter.data;

import java.util.Date;

public class PersonObservationData {
    private String conceptName;
    private Double value;
    private Date observationDate;

    public PersonObservationData(String conceptName, Double value, Date observationDate) {
        this.conceptName = conceptName;
        this.value = value;
        this.observationDate = observationDate;
    }

    public PersonObservationData() {
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public Date getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(Date observationDate) {
        this.observationDate = observationDate;
    }
}