package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;

import java.util.ArrayList;
import java.util.List;

public class ConceptRow extends CSVEntity {
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

    @CSVHeader(name = "datatype")
    public String dataType;

    @CSVRegexHeader(pattern = "synonym.*")
    public List<KeyValue> synonyms;

    @CSVRegexHeader(pattern = "answer.*")
    public List<KeyValue> answers;

    public List<KeyValue> getSynonyms() {
        return synonyms == null ? new ArrayList<KeyValue>() : synonyms;
    }

    public List<KeyValue> getAnswers() {
        return answers == null ? new ArrayList<KeyValue>() : answers;
    }

    public String getDataType() {
        return StringUtils.isEmpty(dataType) ? "N/A" : dataType;
    }

    public String getDescription() {
        return StringUtils.isEmpty(description) ? null : description;
    }

    public String getReferenceTermSource() {
        return StringUtils.isEmpty(referenceTermSource) ? null : referenceTermSource;
    }

    public String getShortName() {
        return StringUtils.isEmpty(shortName) ? null : shortName;
    }

    public String getReferenceTermRelationship() {
        return StringUtils.isEmpty(referenceTermRelationship) ? null : referenceTermRelationship;
    }

    public String getReferenceTermCode() {
        return referenceTermCode;
    }
}
