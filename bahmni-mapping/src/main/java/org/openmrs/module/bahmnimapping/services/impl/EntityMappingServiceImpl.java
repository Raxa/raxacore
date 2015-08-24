package org.openmrs.module.bahmnimapping.services.impl;

import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.module.bahmnimapping.model.EntityMappingType;
import org.openmrs.module.bahmnimapping.services.EntityMappingService;
import org.springframework.beans.factory.annotation.Autowired;

public class EntityMappingServiceImpl implements EntityMappingService{

    private final EntityMapping entityMapping;
    private final EntityMappingType entityMappingType;

    @Autowired
    public EntityMappingServiceImpl(EntityMapping entityMapping,EntityMappingType entityMappingType){
        this.entityMapping = entityMapping;
        this.entityMappingType = entityMappingType;
    }

}
