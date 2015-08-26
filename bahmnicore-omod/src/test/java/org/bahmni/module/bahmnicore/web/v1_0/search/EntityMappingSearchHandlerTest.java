package org.bahmni.module.bahmnicore.web.v1_0.search;

import org.bahmni.module.bahmnicore.contract.entityMapping.Entity;
import org.bahmni.module.bahmnicore.dao.AbstractDao;
import org.junit.Before;
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
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityMappingSearchHandlerTest {

    @Mock
    EntityMappingDao entityMappingDao;
    @Mock
    AbstractDao abstractDao;
    @Mock
    RequestContext requestContext;

    @InjectMocks
    EntityMappingSearchHandler entityMappingController;

    String entityTypeName = "program_obsTemplate";
    String entity1Uuid = "entity1-uuid";
    String entity2Uuid = "entity2-uuid";
    EntityMappingType programObsTemplate;

    @Before
    public void setUp() throws Exception {
        when(requestContext.getParameter("mappingType")).thenReturn(entityTypeName);
        when(requestContext.getParameter("entityUuid")).thenReturn(entity1Uuid);
        programObsTemplate = EntityMappingType.builder().id(1).entity1Type("org.openmrs.Program")
                .entity2Type("org.openmrs.Concept").name(entityTypeName).build();

        when(entityMappingDao.getEntityMappingTypeByName(entityTypeName)).thenReturn(programObsTemplate);

    }

    @Test
    public void shouldGetEntityWithMappingsWhenThereAreEntityMappings() throws Exception {
        EntityMapping entityMapping = EntityMapping.builder().entity1Uuid(entity1Uuid).entity2Uuid(entity2Uuid)
                .entityMappingType(programObsTemplate).build();

        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(asList(entityMapping));

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(entity1Uuid, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(entity2Uuid, Concept.class)).thenReturn(concept);

        AlreadyPaged pageableResult = (AlreadyPaged) entityMappingController.search(requestContext);
        Entity entityWithMappings = (Entity) pageableResult.getPageOfResults().get(0);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity());
        assertEquals(asList(concept), entityWithMappings.getMappings());
    }

    @Test
    public void shouldGetEntityWithZeroMappingsWhenThereIsNoEntityMapping() throws Exception {
        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(new ArrayList<EntityMapping>());

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(entity1Uuid, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(entity2Uuid, Concept.class)).thenReturn(concept);

        AlreadyPaged pageableResult = (AlreadyPaged) entityMappingController.search(requestContext);
        Entity entityWithMappings = (Entity) pageableResult.getPageOfResults().get(0);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity());
        assertEquals(new ArrayList<EntityMapping>(), entityWithMappings.getMappings());

    }

    @Test
    public void shouldGetWithZeroMappingsWhenThereIsNoEntityMappingType() throws Exception {

        when(entityMappingDao.getEntityMappingTypeByName(entityTypeName)).thenReturn(null);
        when(entityMappingDao.getEntityMappings(entity1Uuid, entityTypeName)).thenReturn(new ArrayList<EntityMapping>());

        PageableResult pageableResult = entityMappingController.search(requestContext);

        assertTrue(pageableResult instanceof EmptySearchResult);
    }

}