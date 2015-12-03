package org.openmrs.module.bahmniemrapi.document.service;

import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.document.contract.VisitDocumentRequest;

public interface VisitDocumentService {
    Visit upload(VisitDocumentRequest visitDocumentRequest);
}
