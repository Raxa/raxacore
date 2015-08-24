package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Concept;
import org.openmrs.User;

public abstract class ConceptNameMixin {

    @JsonIgnore
    public abstract Concept getConcept();
}
