package org.bahmni.module.referencedata.labconcepts.contract;

import javax.validation.constraints.NotNull;

public class ConceptCommon {
    private String uniqueName;
    private String displayName;
    private String description;
    private String className;
    private ConceptReferenceTerm conceptReferenceTerm;

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


}
