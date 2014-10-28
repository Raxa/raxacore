package org.openmrs.module.bahmniemrapi.document.contract;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;

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

    public boolean isNew() {
        return StringUtils.isBlank(getObsUuid());
    }

    public boolean shouldVoidDocument() {
        return !StringUtils.isBlank(getObsUuid()) && isVoided();
    }

    public boolean hasConceptChanged(String referenceUuid) {
        return !referenceUuid.equals(getTestUuid());
    }
}
