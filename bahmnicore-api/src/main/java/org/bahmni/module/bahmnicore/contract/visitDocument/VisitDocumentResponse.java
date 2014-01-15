package org.bahmni.module.bahmnicore.contract.visitDocument;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VisitDocumentResponse {
    private String visitUuid;

    public VisitDocumentResponse(String visitUuid) {
        this.visitUuid = visitUuid;
    }
}
