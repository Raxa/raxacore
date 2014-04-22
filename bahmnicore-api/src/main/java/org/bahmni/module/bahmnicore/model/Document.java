package org.bahmni.module.bahmnicore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Document {
    String image;
    String format;
    String testUuid;
    String obsUuid;
    Date obsDateTime;
    boolean voided;

    public Document() {
    }
}
