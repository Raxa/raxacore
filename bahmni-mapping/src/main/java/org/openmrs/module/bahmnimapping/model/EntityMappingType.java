package org.openmrs.module.bahmnimapping.model;

import lombok.Data;

@Data
public class EntityMappingType {
    private Integer id;
    private String name;
    private String entity1Type;
    private String entity2Type;
}
