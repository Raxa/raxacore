package org.bahmni.module.bahmnicore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Document {
    String image;
    String format;
    String testUuid;
    String obsUuid;
    boolean voided;

    public Document() {
    }
}
