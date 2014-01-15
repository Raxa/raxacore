package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentRequest;
import org.openmrs.Visit;

public interface VisitDocumentService {
    public Visit upload(VisitDocumentRequest visitDocumentRequest);
}
