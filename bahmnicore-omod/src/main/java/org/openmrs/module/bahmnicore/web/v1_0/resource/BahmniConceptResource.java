package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

import java.util.ArrayList;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/concept", supportedClass = Concept.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*"}, order = 1)
public class BahmniConceptResource extends ConceptResource1_9 {

    public BahmniConceptResource() {
        allowedMissingProperties.add("hiNormal");
        allowedMissingProperties.add("hiAbsolute");
        allowedMissingProperties.add("hiCritical");
        allowedMissingProperties.add("lowNormal");
        allowedMissingProperties.add("lowAbsolute");
        allowedMissingProperties.add("lowCritical");
        allowedMissingProperties.add("units");
        allowedMissingProperties.add("precise");
    }

    @Override
    public Concept getByUniqueId(String uuidOrName) {
        Concept byUniqueId = super.getByUniqueId(uuidOrName);
        if (byUniqueId != null) {
            return byUniqueId;
        }

        return Context.getConceptService().getConceptByName(uuidOrName);
    }
}
