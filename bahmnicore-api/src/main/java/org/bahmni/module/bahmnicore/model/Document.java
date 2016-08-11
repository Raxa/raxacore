package org.bahmni.module.bahmnicore.model;

public class Document {
    private String image;
    private String format;
    private String encounterTypeName;
    private String patientUuid;
    private String fileType;

    public Document() {
    }

    public Document(String image, String format, String encounterTypeName, String patientUuid, String fileType) {
        this.image = image;
        this.format = format;
        this.encounterTypeName = encounterTypeName;
        this.patientUuid = patientUuid;
        this.fileType = fileType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEncounterTypeName() {
        return encounterTypeName;
    }

    public void setEncounterTypeName(String encounterTypeName) {
        this.encounterTypeName = encounterTypeName;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

