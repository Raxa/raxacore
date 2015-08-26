package org.bahmni.module.bahmnicore.web.v1_0.search;

import org.bahmni.module.bahmnicore.contract.entityMapping.Entity;
import org.bahmni.module.bahmnicore.dao.AbstractDao;
import org.openmrs.module.bahmnimapping.dao.EntityMappingDao;
import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.module.bahmnimapping.model.EntityMappingType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EntityMappingSearchHandler implements SearchHandler {
    private EntityMappingDao entityMappingDao;

    private AbstractDao abstractDao;

    @Autowired
    public EntityMappingSearchHandler(EntityMappingDao entityMappingDao, AbstractDao abstractDao) {
        this.entityMappingDao = entityMappingDao;
        this.abstractDao = abstractDao;
    }

    @Override
    public SearchConfig getSearchConfig() {
        return new SearchConfig("byEntityAndMappingType", RestConstants.VERSION_1 + "/entitymapping", Arrays.asList("1.9.*", "1.10.*", "1.11.*"),
                new SearchQuery.Builder("Allows you to find entity relationships of entity with specific mapping type").withRequiredParameters("entityUuid", "mappingType").build());
    }

    @Override
    public PageableResult search(RequestContext requestContext) throws ResponseException {
        String entityMappingTypeName = requestContext.getParameter("mappingType");
        String entityUuid = requestContext.getParameter("entityUuid");
        EntityMappingType entityMappingType = entityMappingDao.getEntityMappingTypeByName(entityMappingTypeName);
        List<EntityMapping> entityMappings = entityMappingDao.getEntityMappings(entityUuid, entityMappingTypeName);
        Class entity1Class = null;
        Class entity2Class = null;
        if (entityMappingType == null) {
            return new EmptySearchResult();
        }

        try {
            entity1Class = Class.forName(entityMappingType.getEntity1Type());
            entity2Class = Class.forName(entityMappingType.getEntity2Type());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        List mappings = new ArrayList();
        Object entity = abstractDao.getByUuid(entityUuid, entity1Class);
        for (EntityMapping entityMapping : entityMappings) {
            Object mappedEntity = abstractDao.getByUuid(entityMapping.getEntity2Uuid(), entity2Class);
            mappings.add(mappedEntity);
        }

        return new AlreadyPaged<>(requestContext, Arrays.asList(new Entity(entity, mappings)), false);

    }
}
