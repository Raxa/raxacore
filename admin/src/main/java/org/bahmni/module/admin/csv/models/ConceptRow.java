package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;

import java.util.ArrayList;
import java.util.List;

public class ConceptRow extends CSVEntity{
    @CSVHeader(name = "name")
    public String name;

    @CSVHeader(name = "description")
    public String description;

    @CSVHeader(name = "class")
    public String conceptClass;

    @CSVHeader(name = "datatype")
    public String dataType;

    @CSVHeader(name = "shortname")
    public String shortName;

    @CSVRegexHeader(pattern = "synonym.*")
    public List<KeyValue> synonyms;

    @CSVHeader(name = "reference-term-source")
    public String referenceTermSource;

    @CSVHeader(name = "reference-term-code")
    public String referenceTermCode;

    @CSVHeader(name = "reference-term-relationship")
    public String referenceTermRelationship;

    @CSVRegexHeader(pattern = "answer.*")
    public List<KeyValue> answers;

    public List<KeyValue> getSynonyms() {
        return synonyms == null ? new ArrayList<KeyValue>() : synonyms;
    }
    public List<KeyValue> getAnswers() {
        return answers == null ? new ArrayList<KeyValue>() : answers;
    }

    public String getDataType() {
        return dataType==null ? "N/A" : dataType;
    }
}
