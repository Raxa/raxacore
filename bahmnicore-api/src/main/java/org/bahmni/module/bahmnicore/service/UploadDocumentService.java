package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.VisitDocumentUpload;
import org.openmrs.Visit;

public interface UploadDocumentService {
    public Visit upload(VisitDocumentUpload visitDocumentUpload);
}
