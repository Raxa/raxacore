package org.bahmni.module.bahmnicore.model;

import lombok.Data;

@Data
public class Document {
    String image;
    String testUuid;

    public Document() {
    }

    public Document(String image, String testUUID) {
        this.image = image;
        this.testUuid = testUUID;
    }
}
