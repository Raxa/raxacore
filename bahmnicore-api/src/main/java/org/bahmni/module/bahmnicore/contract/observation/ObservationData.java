package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Obs;
import org.openmrs.api.context.Context;

import java.util.Date;

public class ObservationData {
    private String concept;
    private String value;
    private String valueDatatype;

    private boolean isAbnormal;
    private long duration;

    private Date obsDateTime;
    private String visitURI;
    private String encounterURI;
    private String patientURI;

    public ObservationData() {
    }

    public ObservationData(Obs obs, String patientURI, String visitURI, String encounterURI) {
        this.visitURI = visitURI;
        this.concept = obs.getConcept().getName().getName();
        this.encounterURI = encounterURI;
        this.patientURI = patientURI;
        this.value = obs.getValueAsString(Context.getLocale());
        this.valueDatatype = obs.getConcept().getDatatype().getName();
        this.obsDateTime = obs.getObsDatetime();
    }

    public String getVisitURI() {
        return visitURI;
    }

    public void setVisitURI(String visit) {
        this.visitURI = visit;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getEncounterURI() {
        return encounterURI;
    }

    public void setEncounterURI(String encounter) {
        this.encounterURI = encounter;
    }

    public String getPatientURI() {
        return patientURI;
    }

    public void setPatientURI(String patientURI) {
        this.patientURI = patientURI;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getObsDateTime() {
        return obsDateTime;
    }

    public void setObsDateTime(Date obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getValueDatatype() {
        return valueDatatype;
    }

    public void setValueDatatype(String valueDatatype) {
        this.valueDatatype = valueDatatype;
    }
}
