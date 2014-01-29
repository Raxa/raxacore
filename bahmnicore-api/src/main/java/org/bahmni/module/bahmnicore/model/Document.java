package org.bahmni.module.bahmnicore.model;

import lombok.Data;

@Data
public class Document {
    String image;
    String format;
    String testUuid;
    String obsUuid;
    boolean voided;

    public Document() {
    }

    public Document(String image, String format, String testUUID, String obsUuid, boolean voided) {
        this.image = image;
        this.format = format;
        this.testUuid = testUUID;
        this.obsUuid = obsUuid;
        this.voided = voided;
    }
}
