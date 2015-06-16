package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.annotation.CSVHeader;

public class RelationshipRow {

    @CSVHeader(name = "Relationship.personB-registration-number")
    private String personB;

    @CSVHeader(name = "Relationship.type-id")
    private String relationshipTypeId;

    @CSVHeader(name = "Relationship.start-date")
    private String startDate;

    @CSVHeader(name = "Relationship.end-date")
    private String endDate;

    public RelationshipRow() {
    }

    public RelationshipRow(String personB, String relationshipTypeId, String startDate, String endDate) {
        this.personB = personB;
        this.relationshipTypeId = relationshipTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(personB) && StringUtils.isBlank(relationshipTypeId);
    }

    public String[] getRowValues() {
        return new String[]{personB, relationshipTypeId, startDate, endDate};
    }

    public RelationshipRow getHeaders() {
        return new RelationshipRow("Relationship.personB-registration-number", "Relationship.type-id","Relationship.start-date", "Relationship.end-date");
    }

    public String getPersonB() {
        return personB;
    }

    public void setPersonB(String personB) {
        this.personB = personB;
    }

    public String getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(String relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
