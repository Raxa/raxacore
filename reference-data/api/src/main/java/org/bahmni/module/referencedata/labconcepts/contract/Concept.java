package org.bahmni.module.referencedata.labconcepts.contract;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Concept extends ConceptCommon{
    private String dataType;
    private List<String> answers;
    private List<String> synonyms;

    public Concept() {
    }

    @NotNull
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getSynonyms() {
        return synonyms == null ? new ArrayList<String>() : synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }


}