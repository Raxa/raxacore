package org.openmrs.module.bahmnimapping.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.bahmnimapping.dao.EntityMappingDao;
import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EntityMappingDaoImplIT extends BaseModuleContextSensitiveTest {

    @Autowired
    private EntityMappingDao entityMappingDao;

    @Before
    public void setupData() throws Exception {
        executeDataSet("entityMappingData.xml");
    }

    @Test
    public void shouldGetAllTheMappingsForTheGivenMappedEntity() {
        List<EntityMapping> entityMappings = entityMappingDao.getEntityMappings("program_obstemplates", "uuid1");

        assertEquals(2, entityMappings.size());
        EntityMapping firstEntity = entityMappings.get(0);
        assertEquals("uuid1", firstEntity.getEntity1Uuid());
        assertEquals("uuid2", firstEntity.getEntity2Uuid());
        assertEquals("program_obstemplates", firstEntity.getEntityMappingType().getName());
        EntityMapping secondEntity = entityMappings.get(1);
        assertEquals("uuid1", secondEntity.getEntity1Uuid());
        assertEquals("uuid3", secondEntity.getEntity2Uuid());
        assertEquals("program_obstemplates", secondEntity.getEntityMappingType().getName());
    }

    @Test
    public void shouldGetNoMappingsForTheGivenNonMappedEntity(){
        List<EntityMapping> entityMappings = entityMappingDao.getEntityMappings("program_obstemplates", "uuid100");

        assertEquals(0,entityMappings.size());
    }

    @Test
    public void shouldGetNoMappingsForTheGivenNonExistingMappingType(){
        List<EntityMapping> entityMappings = entityMappingDao.getEntityMappings("some_random_non_existing mapping type", "uuid1");

        assertEquals(0,entityMappings.size());
    }
}