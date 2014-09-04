package org.bahmni.module.referencedata.web.contract;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.Date;

public class Sample {
    private String id;
    private String name;
    private String shortName;
    private Boolean isActive;
    private Date dateCreated;
    private Date lastUpdated;
    private Double sortOrder;

    public Sample() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }
}
