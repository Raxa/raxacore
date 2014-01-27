package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataConcept {
    String uuid;
    String name;
    String description;
    String className;
    String dataTypeUuid;
}
