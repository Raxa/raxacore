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
import java.util.UUID;

import static org.bahmni.module.admin.csv.utils.CSVUtils.getStringArray;

public class ConceptRow extends CSVEntity {
    @CSVHeader(name = "uuid", optional = true)
    public String uuid;

    @CSVHeader(name = "name")
    public String name;

    @CSVHeader(name = "description", optional = true)
    public String description;

    @CSVHeader(name = "class")
    public String conceptClass;

    @CSVHeader(name = "shortname")
    public String shortName;

    @CSVHeader(name = "reference-term-source", optional = true)
    public String referenceTermSource;

    @CSVHeader(name = "reference-term-code", optional = true)
    public String referenceTermCode;

    @CSVHeader(name = "reference-term-relationship", optional = true)
    public String referenceTermRelationship;

    @CSVHeader(name = "datatype")
    public String dataType;

    @CSVRegexHeader(pattern = "synonym.*")
    public List<KeyValue> synonyms;

    @CSVRegexHeader(pattern = "answer.*")
    public List<KeyValue> answers;

    @CSVHeader(name = "units", optional = true)
    public String units;

    @CSVHeader(name = "High Normal", optional = true)
    public String hiNormal;

    @CSVHeader(name = "Low Normal", optional = true)
    public String lowNormal;

    public ConceptRow(String uuid, String name, String description, String conceptClass, String shortName, String referenceTermCode, String referenceTermRelationship, String referenceTermSource, String dataType, String units, String hiNormal, String lowNormal, List<KeyValue> synonyms, List<KeyValue> answers) {
        this.uuid = uuid;
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
        this.units = units;
        this.hiNormal = hiNormal;
        this.lowNormal = lowNormal;
        String[] aRow = {uuid, name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource, dataType, units, hiNormal, lowNormal};
        String[] synonymsRow = getStringArray(synonyms);
        String[] answersRow = getStringArray(answers);
        aRow = ArrayUtils.addAll(aRow, ArrayUtils.addAll(synonymsRow, answersRow));
        originalRow(aRow);
    }

    public ConceptRow getHeaders(){
        int synonymCount = 1, answerCount = 1;
        List<KeyValue> synonymHeaders = new ArrayList<>();
        List<KeyValue> answerHeaders = new ArrayList<>();
        for (KeyValue ignored : synonyms) {
            synonymHeaders.add(new KeyValue("synonymHeader", "synonym." + synonymCount));
            synonymCount++;
        }
        for (KeyValue ignored : answers) {
            answerHeaders.add(new KeyValue("answerHeader", "answer." + answerCount));
            answerCount++;
        }
        return new ConceptRow("uuid", "name", "description", "class", "shortname", "reference-term-code", "reference-term-relationship", "reference-term-source", "datatype", "units", "High Normal", "Low Normal", synonymHeaders, answerHeaders);
    }

    public ConceptRow() {
    }

    public List<KeyValue> getSynonyms() {
        return synonyms == null ? new ArrayList<KeyValue>() : synonyms;
    }

    public List<KeyValue> getAnswers() {
        return answers == null ? new ArrayList<KeyValue>() : answers;
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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getHiNormal() {
        return hiNormal;
    }

    public void setHiNormal(String hiNormal) {
        this.hiNormal = hiNormal;
    }

    public String getLowNormal() {
        return lowNormal;
    }

    public void setLowNormal(String lowNormal) {
        this.lowNormal = lowNormal;
    }

    public String getReferenceTermCode() {
        return referenceTermCode;
    }

    public void adjust(int maxSynonyms, int maxAnswers) {
        addBlankSynonyms(maxSynonyms);
        addBlankAnswers(maxAnswers);
        String[] aRow = {uuid, name, description, conceptClass, shortName, referenceTermCode, referenceTermRelationship, referenceTermSource, dataType, units, hiNormal, lowNormal};
        String[] synonymsRow = getStringArray(synonyms);
        String[] answersRow = getStringArray(answers);
        aRow = ArrayUtils.addAll(aRow, ArrayUtils.addAll(synonymsRow, answersRow));
        originalRow(aRow);
    }

    private void addBlankAnswers(int maxAnswers) {
        int counter  = this.getAnswers().size();
        this.answers = this.getAnswers();
        while (counter <= maxAnswers){
            this.answers.add(new KeyValue("answer", ""));
            counter++;
        }
    }

    private void addBlankSynonyms(int maxSynonyms) {
        int counter = this.getSynonyms().size();
        this.synonyms = this.getSynonyms();
        while (counter <= maxSynonyms){
            this.synonyms.add(new KeyValue("synonym", ""));
            counter++;
        }
    }
}
