package org.bahmni.module.bahmnicore.model;

import lombok.Data;

@Data
public class Document {
    String image;
    String format;
    String testUuid;

    public Document() {
    }

    public Document(String image, String format, String testUUID) {
        this.image = image;
        this.format = format;
        this.testUuid = testUUID;
    }
}
