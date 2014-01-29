package org.bahmni.module.referncedatafeedclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class ReferenceDataConcept {
    private final String uuid;
    private final String name;
    private final String description;
    private final String className;
    private final String dataTypeUuid;
    private String shortName;
    Set<String> setMemberUuids = new HashSet<>();

    public ReferenceDataConcept(String uuid, String name, String description, String className, String dataTypeUuid) {
        this(uuid, name, description, className, dataTypeUuid, null);
    }

    public ReferenceDataConcept(String uuid, String name, String description, String className, String dataTypeUuid, String shortName) {
        this(uuid, name, description, className, dataTypeUuid, shortName, new HashSet<String>());
    }
}
