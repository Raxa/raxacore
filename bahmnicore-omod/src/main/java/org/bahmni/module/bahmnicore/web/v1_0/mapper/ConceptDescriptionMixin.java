package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Concept;

public abstract class ConceptDescriptionMixin {

    @JsonIgnore
    public abstract Concept getConcept();
}
