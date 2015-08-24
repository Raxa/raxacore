package org.openmrs.module.bahmnimapping.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityMappingType {
    private Integer id;
    private String name;
    private String entity1Type;
    private String entity2Type;
}
