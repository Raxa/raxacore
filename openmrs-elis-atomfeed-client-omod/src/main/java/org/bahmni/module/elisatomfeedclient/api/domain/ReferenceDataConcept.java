package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@AllArgsConstructor
public class ReferenceDataConcept {
    private final String uuid;
    private final String name;
    private final String description;
    private final String className;
    private final String dataTypeUuid;
    private String shortName;

    public ReferenceDataConcept(String uuid, String name, String description, String className, String dataTypeUuid) {
        this(uuid, name, description, className, dataTypeUuid, null);
    }
}
