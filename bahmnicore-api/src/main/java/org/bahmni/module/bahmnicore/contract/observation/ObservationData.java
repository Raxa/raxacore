package org.bahmni.module.bahmnicore.contract.observation;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.List;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ObservationData {
    private int conceptSortWeight;
    private String concept;
    private String value;
    private String type;
    private String unit;

    private Boolean isAbnormal;
    private Long duration;

    private Date time;
    private LinkData links;
    private String rootConcept;

    public ObservationData() {
    }

    public ObservationData(Obs obs, String patientURI, String visitURI, String encounterURI, List<String> providerURIs, int conceptSortWeight) {
        this.concept = obs.getConcept().getName().getName();
        this.conceptSortWeight = conceptSortWeight;
        this.value = obs.getValueAsString(Context.getLocale());
        this.type = obs.getConcept().getDatatype().getName();
        this.time = obs.getObsDatetime();
        this.links = new LinkData(visitURI, encounterURI, patientURI, providerURIs);
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


}
