package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.openmrs.Concept;
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

    @RepHandler(value = NamedRepresentation.class, name = "conceptsetconfig")
    public SimpleObject asConfig(Concept delegate) throws ConversionException {
        SimpleObject simpleObject = this.asFullChildren(delegate);
        List<SimpleObject> setMembers = ((List<SimpleObject>) simpleObject.get("setMembers"));

        List<SimpleObject> attributes = new ArrayList<>();

        for (SimpleObject setMember : setMembers) {
            if (((SimpleObject) setMember.get("conceptClass")).get("name").equals("Concept Attribute")) {
                attributes.add(setMember);
            }
        }

        setMembers.removeAll(attributes);
        simpleObject.put("attributes", attributes);
        return simpleObject;
    }

}
