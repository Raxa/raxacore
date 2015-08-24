package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.entityMapping.Entity;
import org.bahmni.module.bahmnicore.dao.AbstractDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.bahmnimapping.dao.EntityMappingDao;
import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.module.bahmnimapping.model.EntityMappingType;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityMappingControllerTest {

    @Mock
    EntityMappingDao entityMappingDao;
    @Mock
    AbstractDao abstractDao;

    @InjectMocks
    EntityMappingController entityMappingController;

    @Test
    public void shouldGetEntityWithMappingsWhenThereAreEntityMappings() throws Exception {
        String entityTypeName = "program_obsTemplate";
        String entity1Uuid = "entity1-uuid";
        String entity2Uuid = "entity2-uuid";
        EntityMappingType programObsTemplate = EntityMappingType.builder().id(1).entity1Type("org.openmrs.Program")
                .entity2Type("org.openmrs.Concept").name(entityTypeName).build();
        EntityMapping entityMapping = EntityMapping.builder().entity1Uuid(entity1Uuid).entity2Uuid(entity2Uuid)
                .entityMappingType(programObsTemplate).build();

        when(entityMappingDao.getEntityMappingTypeByName(entityTypeName)).thenReturn(programObsTemplate);
        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(asList(entityMapping));

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(entity1Uuid, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(entity2Uuid, Concept.class)).thenReturn(concept);

        Entity entityWithMappings = entityMappingController.getEntityWithMappings(entity1Uuid, entityTypeName);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity1());
        assertEquals(asList(concept), entityWithMappings.getMappings());
    }
    
    @Test
    public void shouldGetEntityWithZeroMappingsWhenThereIsNoEntityMapping() throws Exception {
        String entityTypeName = "program_obsTemplate";
        String entity1Uuid = "entity1-uuid";
        String entity2Uuid = "entity2-uuid";
        EntityMappingType programObsTemplate = EntityMappingType.builder().id(1).entity1Type("org.openmrs.Program")
                .entity2Type("org.openmrs.Concept").name(entityTypeName).build();

        when(entityMappingDao.getEntityMappingTypeByName(entityTypeName)).thenReturn(programObsTemplate);
        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(new ArrayList<EntityMapping>());

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(entity1Uuid, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(entity2Uuid, Concept.class)).thenReturn(concept);

        Entity entityWithMappings = entityMappingController.getEntityWithMappings(entity1Uuid, entityTypeName);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity1());
        assertEquals(new ArrayList<EntityMapping>(), entityWithMappings.getMappings());

    }
    
    @Test
    public void shouldGetWithZeroMappingsWhenThereIsNoEntityMappingType() throws Exception {
        String entityTypeName = "program_obsTemplate";
        String entity1Uuid = "entity1-uuid";

        when(entityMappingDao.getEntityMappingTypeByName(entityTypeName)).thenReturn(null);
        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(new ArrayList<EntityMapping>());

        Entity entityWithMappings = entityMappingController.getEntityWithMappings(entity1Uuid, entityTypeName);

        assertNull(entityWithMappings);
    }
}