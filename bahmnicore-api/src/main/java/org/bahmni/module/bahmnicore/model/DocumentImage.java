package org.bahmni.module.bahmnicore.model;

public class DocumentImage {
    private String image;
    private String format;
    private String encounterTypeName;
    private String patientUuid;

    public DocumentImage() {
    }

    public DocumentImage(String image, String format, String encounterTypeName, String patientUuid) {
        this.image = image;
        this.format = format;
        this.encounterTypeName = encounterTypeName;
        this.patientUuid = patientUuid;
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
}

