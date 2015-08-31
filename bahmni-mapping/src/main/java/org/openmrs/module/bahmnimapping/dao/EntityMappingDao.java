package org.openmrs.module.bahmnimapping.dao;

import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.module.bahmnimapping.model.EntityMappingType;

import java.util.List;

public interface EntityMappingDao {
    List<EntityMapping> getEntityMappings(String entity1Uuid, String mappingTypeName);
    EntityMappingType getEntityMappingTypeByName(String name);
}
