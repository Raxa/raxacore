package org.bahmni.module.referencedata.labconcepts.contract;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ConceptCommon {
    private String uniqueName;
    private String displayName;
    private String description;
    private String className;
    private String dataType;
    private ConceptReferenceTerm conceptReferenceTerm;
    private String uuid;

    public ConceptCommon(String uuid, String name, String conceptDescription, String conceptClass, String conceptShortname, String conceptReferenceTermCode, String conceptReferenceTermRelationship, String conceptReferenceTermSource, String dataType) {
        this.uuid = uuid;
        this.uniqueName = name;
        this.description = conceptDescription;
        this.className = conceptClass;
        this.displayName = conceptShortname;
        this.dataType = dataType;
        this.conceptReferenceTerm = new ConceptReferenceTerm(conceptReferenceTermCode, conceptReferenceTermRelationship, conceptReferenceTermSource);
    }

    public ConceptCommon() {

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

    public String getDataType() {
        return StringUtils.isEmpty(dataType) ? "N/A" : dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
