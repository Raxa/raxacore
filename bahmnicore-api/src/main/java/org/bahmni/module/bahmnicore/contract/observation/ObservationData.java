package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Obs;

import java.util.Date;

public class ObservationData {
    private VisitData visit;
    private ConceptData concept;
    private EncounterData encounter;
    private PatientData patient;
    private ValueData valueData;
    private Date obsDateTime;
    private boolean isAbnormal;
    private long duration;

    public ObservationData() {
    }

    public ObservationData(Obs obs) {
        this.visit = new VisitData(obs.getEncounter().getVisit());
        this.concept = new ConceptData(obs.getConcept());
        this.encounter = new EncounterData(obs.getEncounter());
        this.patient = new PatientData(obs.getPerson());
        this.valueData = new ValueData(obs);
        this.obsDateTime = obs.getObsDatetime();
    }

    public VisitData getVisit() {
        return visit;
    }

    public void setVisit(VisitData visit) {
        this.visit = visit;
    }

    public ConceptData getConcept() {
        return concept;
    }

    public void setConcept(ConceptData concept) {
        this.concept = concept;
    }

    public EncounterData getEncounter() {
        return encounter;
    }

    public void setEncounter(EncounterData encounter) {
        this.encounter = encounter;
    }

    public PatientData getPatient() {
        return patient;
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
    }

    public ValueData getValueData() {
        return valueData;
    }

    public void setValueData(ValueData value) {
        this.valueData = value;
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

}
