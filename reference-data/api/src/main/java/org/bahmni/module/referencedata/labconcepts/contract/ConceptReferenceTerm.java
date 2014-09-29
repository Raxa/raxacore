package org.bahmni.module.referencedata.labconcepts.contract;

public class ConceptReferenceTerm {

    private String referenceTermName;
    private String referenceTermCode;
    private String referenceTermRelationship;
    private String referenceTermSource;

    public String getReferenceTermName() {
        return referenceTermName;
    }

    public void setReferenceTermName(String referenceTermName) {
        this.referenceTermName = referenceTermName;
    }

    public String getReferenceTermCode() {
        return referenceTermCode;
    }

    public void setReferenceTermCode(String referenceTermCode) {
        this.referenceTermCode = referenceTermCode;
    }

    public String getReferenceTermRelationship() {
        return referenceTermRelationship;
    }

    public void setReferenceTermRelationship(String referenceTermRelationship) {
        this.referenceTermRelationship = referenceTermRelationship;
    }

    public String getReferenceTermSource() {
        return referenceTermSource;
    }

    public void setReferenceTermSource(String referenceTermSource) {
        this.referenceTermSource = referenceTermSource;
    }
}
