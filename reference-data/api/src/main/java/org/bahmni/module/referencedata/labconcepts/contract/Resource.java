package org.bahmni.module.referencedata.labconcepts.contract;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.Date;

public class Resource {
    private String id;
    private Date dateCreated;
    private Date lastUpdated;
    private String name;
    private Boolean isActive;

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