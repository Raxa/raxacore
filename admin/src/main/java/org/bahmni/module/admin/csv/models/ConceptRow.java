package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.module.admin.csv.utils.CSVUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.admin.csv.utils.CSVUtils.getStringArray;

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

    public ConceptRow(String name, String description, String conceptClass, String shortName, String referenceTermCode, String referenceTermRelationship, String referenceTermSource, String dataType, List<KeyValue> synonyms, List<KeyValue> answers) {
        this.name = name;
        this.description = description;
        this.conceptClass = conceptClass;
        this.shortName = shortName;
        this.referenceTermCode = referenceTermCode;
        this.referenceTermRelationship = referenceTermRelationship;
        this.referenceTermSource = referenceTermSource;
        this.dataType = dataType;
        this.synonyms = synonyms;
        this.answers = answers;
        String[] aRow = {name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource, dataType};
        String[] synonymsRow = getStringArray(synonyms);
        String[] answersRow = getStringArray(answers);
        aRow = ArrayUtils.addAll(aRow, ArrayUtils.addAll(synonymsRow, answersRow));
        originalRow(aRow);
    }

    public static ConceptRow getHeaders(){
        return new ConceptRow("name", "description", "class", "shortname", "reference-term-code", "reference-term-relationship", "reference-term-source", "datatype", null, null);
    }

    public ConceptRow() {
    }

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
