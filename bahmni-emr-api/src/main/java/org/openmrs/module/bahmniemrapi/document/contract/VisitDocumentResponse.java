package org.openmrs.module.bahmniemrapi.document.contract;

public class VisitDocumentResponse {
    private String visitUuid;

    public VisitDocumentResponse(String visitUuid) {
        this.visitUuid = visitUuid;
    }

    public VisitDocumentResponse() {
    }

    public String getVisitUuid() {
        return visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }
}
