package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.entityMapping.Entity;
import org.bahmni.module.bahmnicore.dao.AbstractDao;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.CustomObjectMapper;
import org.openmrs.module.bahmnimapping.dao.EntityMappingDao;
import org.openmrs.module.bahmnimapping.model.EntityMapping;
import org.openmrs.module.bahmnimapping.model.EntityMappingType;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/entityMappings")
public class EntityMappingController extends BaseRestController{

    @Autowired
    private EntityMappingDao entityMappingDao;

    @Autowired
    private AbstractDao abstractDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getEntityWithMappings(@RequestParam(value = "entity1Uuid", required = true) String entity1Uuid,
                                        @RequestParam(value = "entityMappingType", required = true) String entityMappingTypeName) throws ClassNotFoundException {

        List mappings = new ArrayList();
        List<EntityMapping> entityMappings = entityMappingDao.getEntityMappings(entity1Uuid, entityMappingTypeName);
        EntityMappingType entityMappingType = entityMappingDao.getEntityMappingTypeByName(entityMappingTypeName);
        if(entityMappingType ==null){
            return null;
        }

        Class entity1Class = Class.forName(entityMappingType.getEntity1Type());
        Class entity2Class = Class.forName(entityMappingType.getEntity2Type());

        Object entity1 = abstractDao.getByUuid(entity1Uuid, entity1Class);
        for (EntityMapping entityMapping : entityMappings) {
            Object mappedEntity = abstractDao.getByUuid(entityMapping.getEntity2Uuid(), entity2Class);
            mappings.add(mappedEntity);
        }
//        ServletOutputStream outputStream = response.getOutputStream();
//        new CustomObjectMapper().writeValue(outputStream, new Entity(entity1, mappings));
//        outputStream.flush();

        try {
            return new CustomObjectMapper().writeValueAsString(new Entity(entity1, mappings));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
