package org.bahmni.module.referencedata.labconcepts.contract;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Concept {
    private String uniqueName;
    private String displayName;
    private String description;
    private String className;
    private String dataType;
    private List<String> answers;
    private List<String> synonyms;
    private ConceptReferenceTerm conceptReferenceTerm;

    public Concept() {
    }

    public ConceptReferenceTerm getConceptReferenceTerm() {
        return conceptReferenceTerm;
    }

    public void setConceptReferenceTerm(ConceptReferenceTerm conceptReferenceTerm) {
        this.conceptReferenceTerm = conceptReferenceTerm;
    }

    @NotNull
    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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