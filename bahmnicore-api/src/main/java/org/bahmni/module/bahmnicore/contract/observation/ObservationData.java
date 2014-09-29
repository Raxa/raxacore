package org.bahmni.module.bahmnicore.contract.observation;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ObservationData {
    private Date encounterTime;
    private int conceptSortWeight;
    private String concept;
    private String conceptShortName;
    private String value;
    private String type;
    private String unit;

    private Boolean isAbnormal = null;
    private Long duration = null;

    private Date time;
    private LinkData links;
    private String rootConcept;

    private Date visitStartDate;

    public ObservationData() {
    }

    public ObservationData(Obs anObservation, String patientURI, String visitURI, String encounterURI, List<String> providerURIs, int conceptSortWeight) {
        this.concept = anObservation.getConcept().getName().getName();
        for (ConceptName conceptName : anObservation.getConcept().getNames()) {
            if (ConceptNameType.SHORT.equals(conceptName.getConceptNameType())) {
                this.conceptShortName = conceptName.getName();
                break;
            }
        }
        this.conceptSortWeight = conceptSortWeight;
        this.value = getValue(anObservation);
        this.type = anObservation.getConcept().getDatatype().getName();
        this.time = anObservation.getObsDatetime();
        this.encounterTime = anObservation.getEncounter().getEncounterDatetime();
        Visit visit = anObservation.getEncounter().getVisit();
        if(visit != null ){
            this.visitStartDate = visit.getStartDatetime();
        }
        this.links = new LinkData(visitURI, encounterURI, patientURI, providerURIs);
    }

    private boolean isNumeric(Obs anObservation) {
        return anObservation.getConcept().getDatatype().getHl7Abbreviation().equals(ConceptDatatype.NUMERIC);
    }

    private String getValue(Obs anObservation) {
        if(isNumeric(anObservation)) {
            return anObservation.getValueNumeric() != null ? anObservation.getValueNumeric().toString() : "";
        }
        return anObservation.getValueAsString(Context.getLocale());
    }

    public Date getEncounterTime() {
        return encounterTime;
    }

    public void setEncounterTime(Date encounterTime) {
        this.encounterTime = encounterTime;
    }

    public int getConceptSortWeight() {
        return conceptSortWeight;
    }

    public void setConceptSortWeight(int conceptSortWeight) {
        this.conceptSortWeight = conceptSortWeight;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date obsDateTime) {
        this.time = obsDateTime;
    }

    public LinkData getLinks() {
        return links;
    }

    public void setLinks(LinkData links) {
        this.links = links;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        if (isAbnormal != null && isAbnormal)
            this.isAbnormal = isAbnormal;
    }

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        if (duration != null && duration != 0)
            this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String valueDatatype) {
        this.type = valueDatatype;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRootConcept() {
        return rootConcept;
    }

    public void setRootConcept(String rootConcept) {
        this.rootConcept = rootConcept;
    }


    public String getConceptShortName() {
        return conceptShortName;
    }

    public void setConceptShortName(String conceptShortName) {
        this.conceptShortName = conceptShortName;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }
}
