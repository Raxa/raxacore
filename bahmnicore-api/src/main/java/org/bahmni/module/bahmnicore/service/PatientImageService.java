package org.bahmni.module.bahmnicore.service;

public interface PatientImageService {
    public void saveImage(String patientIdentifier, String image);
    public String saveDocument(Integer patientId, String encounterTypeName, String images);

}
