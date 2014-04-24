package org.bahmni.module.referencedatafeedclient.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ReferenceDataConcept {
    private final String uuid;
    private final String name;
    private final String className;
    private final String dataTypeUuid;
    private String shortName;
    private String description;
    private boolean retired = false;
    private boolean set = false;
    Set<String> setMemberUuids = new HashSet<>();
    private String testUnitOfMeasure;

    public ReferenceDataConcept(String uuid, String name, String className, String dataTypeUuid) {
        this.uuid = uuid;
        this.name = name;
        this.className = className;
        this.dataTypeUuid = dataTypeUuid;
    }
}
