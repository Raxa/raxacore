package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Concept;
import org.openmrs.User;

import java.util.List;

public abstract class ConceptMixin {

    @JsonIgnore
    public abstract Boolean getRetired();

    @JsonIgnore
    public abstract User getCreator();

    @JsonIgnore
    public abstract User getChangedBy();

    @JsonIgnore
    public abstract Boolean getSet();

    @JsonIgnore
    public abstract Boolean getLocalePreferred();

    @JsonIgnore
    public abstract Boolean getVoided();

    @JsonIgnore
    public abstract List<Concept> getPossibleValues();

    @JsonIgnore
    public abstract Concept getConcept();
}