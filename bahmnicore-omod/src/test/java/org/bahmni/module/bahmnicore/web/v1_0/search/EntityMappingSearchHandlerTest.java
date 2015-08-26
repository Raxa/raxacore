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
import java.util.Collections;

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
    EntityMappingSearchHandler entityMappingSearchHandler;

    String PROGRAM_OBS_TEMPLATE = "program_obsTemplate";
    String ENTITY1_UUID = "entity1-uuid";
    String ENTITY2_UUID = "entity2-uuid";
    EntityMappingType programObsTemplateMappingType;

    @Before
    public void setUp() throws Exception {
        when(requestContext.getParameter("mappingType")).thenReturn(PROGRAM_OBS_TEMPLATE);
        when(requestContext.getParameter("entityUuid")).thenReturn(ENTITY1_UUID);
        programObsTemplateMappingType = EntityMappingType.builder().id(1).entity1Type("org.openmrs.Program")
                .entity2Type("org.openmrs.Concept").name(PROGRAM_OBS_TEMPLATE).build();

        when(entityMappingDao.getEntityMappingTypeByName(PROGRAM_OBS_TEMPLATE)).thenReturn(programObsTemplateMappingType);

    }

    @Test
    public void shouldGetEntityWithMappingsWhenThereAreEntityMappings() throws Exception {
        EntityMapping entityMapping = EntityMapping.builder().entity1Uuid(ENTITY1_UUID).entity2Uuid(ENTITY2_UUID)
                .entityMappingType(programObsTemplateMappingType).build();

        when(entityMappingDao.getEntityMappings(ENTITY1_UUID, PROGRAM_OBS_TEMPLATE)).thenReturn(Collections.singletonList(entityMapping));

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(ENTITY1_UUID, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(ENTITY2_UUID, Concept.class)).thenReturn(concept);

        AlreadyPaged pageableResult = (AlreadyPaged) entityMappingSearchHandler.search(requestContext);
        Entity entityWithMappings = (Entity) pageableResult.getPageOfResults().get(0);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity());
        assertEquals(Collections.singletonList(concept), entityWithMappings.getMappings());
    }

    @Test
    public void shouldGetEntityWithZeroMappingsWhenThereIsNoEntityMapping() throws Exception {
        when(entityMappingDao.getEntityMappings(ENTITY1_UUID, PROGRAM_OBS_TEMPLATE)).thenReturn(new ArrayList<EntityMapping>());

        Program program = new Program();
        Concept concept = new Concept();

        when(abstractDao.getByUuid(ENTITY1_UUID, Program.class)).thenReturn(program);
        when(abstractDao.getByUuid(ENTITY2_UUID, Concept.class)).thenReturn(concept);

        AlreadyPaged pageableResult = (AlreadyPaged) entityMappingSearchHandler.search(requestContext);
        Entity entityWithMappings = (Entity) pageableResult.getPageOfResults().get(0);

        assertNotNull(entityWithMappings);
        assertEquals(program, entityWithMappings.getEntity());
        assertEquals(new ArrayList<EntityMapping>(), entityWithMappings.getMappings());

    }

    @Test
    public void shouldGetWithZeroMappingsWhenThereIsNoEntityMappingType() throws Exception {

        when(entityMappingDao.getEntityMappingTypeByName(PROGRAM_OBS_TEMPLATE)).thenReturn(null);
        when(entityMappingDao.getEntityMappings(ENTITY1_UUID, PROGRAM_OBS_TEMPLATE)).thenReturn(new ArrayList<EntityMapping>());

        PageableResult pageableResult = entityMappingSearchHandler.search(requestContext);

        assertTrue(pageableResult instanceof EmptySearchResult);
    }

}