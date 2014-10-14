package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.ArrayUtils;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;

import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.admin.csv.utils.CSVUtils.getStringArray;

public class ConceptSetRow extends CSVEntity {
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

    public ConceptSetRow(String name, String description, String conceptClass, String shortName, String referenceTermCode, String referenceTermRelationship, String referenceTermSource, List<KeyValue> children) {
        this.name = name;
        this.description = description;
        this.conceptClass = conceptClass;
        this.shortName = shortName;
        this.referenceTermCode = referenceTermCode;
        this.referenceTermRelationship = referenceTermRelationship;
        this.referenceTermSource = referenceTermSource;
        this.children = children;
        String[] aRow = {name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource};
        String[] childrenRow = getStringArray(children);
        aRow = ArrayUtils.addAll(aRow, childrenRow);
        originalRow(aRow);
    }

    public ConceptSetRow() {
    }
}
