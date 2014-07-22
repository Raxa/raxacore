package org.openmrs.module.bahmniemrapi.document.contract;

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
