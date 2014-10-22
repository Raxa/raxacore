package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bahmni.module.admin.csv.utils.CSVUtils.getStringArray;

public class ConceptSetRow extends CSVEntity {
    @CSVHeader(name = "uuid")
    public String uuid;

    @CSVHeader(name = "name")
    public String name;

    @CSVHeader(name = "description")
    public String description;

    @CSVHeader(name = "class")
    public String conceptClass;

    @CSVHeader(name = "shortname")
    public String shortName;

    @CSVHeader(name = "reference-term-source")
    public String referenceTermSource;

    @CSVHeader(name = "reference-term-code")
    public String referenceTermCode;

    @CSVHeader(name = "reference-term-relationship")
    public String referenceTermRelationship;

    @CSVRegexHeader(pattern = "child.*")
    public List<KeyValue> children;

    public List<KeyValue> getChildren() {
        return children == null ? new ArrayList<KeyValue>() : children;
    }

    public String getShortName() {
        return (shortName != null && StringUtils.isEmpty(shortName.trim())) ? null : shortName;
    }

    public String getName() {
        return name == null ? null : name.trim();
    }

    public String getConceptClass() {
        return conceptClass == null ? null : conceptClass.trim();
    }

    public String getUuid() {
        try{
            UUID uuid = UUID.fromString(this.uuid.trim());
            return uuid.toString();
        } catch (Exception e){
            return null;
        }
    }

    public ConceptSetRow getHeaders(){
        int childCount = 1;
        List<KeyValue> childHeaders = new ArrayList<>();
        for (KeyValue ignored : children) {
            childHeaders.add(new KeyValue("childHeader", "child." + childCount));
            childCount++;
        }
        return new ConceptSetRow("uuid", "name", "description", "class", "shortname", "reference-term-code", "reference-term-relationship", "reference-term-source", childHeaders);
    }

    public ConceptSetRow(String uuid, String name, String description, String conceptClass, String shortName, String referenceTermCode, String referenceTermRelationship, String referenceTermSource, List<KeyValue> children) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.conceptClass = conceptClass;
        this.shortName = shortName;
        this.referenceTermCode = referenceTermCode;
        this.referenceTermRelationship = referenceTermRelationship;
        this.referenceTermSource = referenceTermSource;
        this.children = children;
        String[] aRow = {uuid, name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource};
        String[] childrenRow = getStringArray(children);
        aRow = ArrayUtils.addAll(aRow, childrenRow);
        originalRow(aRow);
    }

    public ConceptSetRow() {
    }

    public void adjust(int maxSetMembers) {
        addBlankChildren(maxSetMembers);
        String[] aRow = {uuid, name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource};
        String[] childrenRow = getStringArray(children);
        aRow = ArrayUtils.addAll(aRow, childrenRow);
        originalRow(aRow);
    }

    private void addBlankChildren(int maxSetMembers) {
        int counter  = this.getChildren().size();
        this.children = this.getChildren();
        while (counter <= maxSetMembers){
            this.children.add(new KeyValue("child", ""));
            counter++;
        }
    }
}
