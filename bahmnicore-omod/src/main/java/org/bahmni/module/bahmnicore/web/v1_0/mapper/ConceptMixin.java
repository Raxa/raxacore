package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.codehaus.jackson.annotate.JsonIgnore;

public abstract class ConceptMixin {

    @JsonIgnore
    public abstract  Boolean getRetired();

    @JsonIgnore
    public abstract  Boolean getSet();
}
