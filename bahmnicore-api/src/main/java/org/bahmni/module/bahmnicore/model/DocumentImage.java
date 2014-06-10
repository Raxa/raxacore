package org.bahmni.module.bahmnicore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentImage {
    String image;
    String format;
    String encounterTypeName;
    String patientUuid;

    public DocumentImage() {
    }
}

