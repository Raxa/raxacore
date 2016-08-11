package org.bahmni.module.bahmnicore.service;

import org.springframework.http.ResponseEntity;

public interface PatientDocumentService {
    public void saveImage(String patientIdentifier, String image);
    public String saveDocument(Integer patientId, String encounterTypeName, String images, String format, String fileType);
    public ResponseEntity<Object> retriveImage(String patientUuid);

}
