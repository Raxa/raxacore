package org.openmrs.module.bahmnimapping.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityMapping {
    private Integer id;
    private String entity1Uuid;
    private String entity2Uuid;
    private EntityMappingType entityMappingType;

}


