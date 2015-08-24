package org.openmrs.module.bahmnimapping.model;

import lombok.Data;

@Data
public class EntityMapping {
    private Integer id;
    private String entity1Uuid;
    private String entity2Uuid;
    private EntityMappingType entityMappingType;
}
