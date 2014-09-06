package org.bahmni.module.referencedata.web.contract;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.Date;

public class Resource {
    String id;
    Date dateCreated;
    Date lastUpdated;
    String name;
    Boolean isActive;
    Double sortOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
