package org.openmrs.module.bahmnimapping.dao;

import org.openmrs.module.bahmnimapping.model.EntityMapping;

import java.util.List;

public interface EntityMappingDao {
    List<EntityMapping> getEntityMappings(String mappingTypeName, String entity1Uuid);
}
