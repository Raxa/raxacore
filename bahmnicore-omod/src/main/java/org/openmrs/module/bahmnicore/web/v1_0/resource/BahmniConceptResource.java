package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.bahmni.module.referencedata.labconcepts.model.ConceptMetaData;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/concept", supportedClass = Concept.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*"}, order = 0)
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
        allowedMissingProperties.add("handler");
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        return super.doSearch(context);
    }

    @Override
    public Concept getByUniqueId(String uuidOrName) {
        Concept byUniqueId = super.getByUniqueId(uuidOrName);
        if (byUniqueId != null) {
            return byUniqueId;
        }

        return Context.getConceptService().getConceptByName(uuidOrName);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {

        DelegatingResourceDescription representationDescription = super.getRepresentationDescription(rep);
        if (representationDescription == null) {
            if (rep instanceof NamedRepresentation && rep.getRepresentation().equals("bahmni")) {
                DelegatingResourceDescription description = new DelegatingResourceDescription();
                description.addProperty("uuid");
                description.addProperty("name");
                description.addProperty("names");
                description.addProperty("set");
                description.addProperty("datatype");
                description.addProperty("conceptClass");
                description.addProperty("hiNormal");
                description.addProperty("lowNormal");
                description.addProperty("hiAbsolute");
                description.addProperty("lowAbsolute");
                description.addProperty("units");
                description.addProperty("handler");
                description.addProperty("descriptions", Representation.DEFAULT);
                description.addProperty("answers", new NamedRepresentation("bahmniAnswer"));
                description.addProperty("setMembers", new NamedRepresentation("bahmni"));
                return description;
            } else if (rep instanceof NamedRepresentation && rep.getRepresentation().equals("bahmniAnswer")) {
                DelegatingResourceDescription description = new DelegatingResourceDescription();
                description.addProperty("uuid", Representation.DEFAULT);
                description.addProperty("name", Representation.DEFAULT);
                description.addProperty("names", Representation.DEFAULT);
                description.addProperty("displayString");
                return description;
            }
        }
        return representationDescription;
    }

    @PropertyGetter("conceptClass")
    public SimpleObject getConceptClassRepresentation(Concept instance) {
        ConceptClass conceptClass = instance.getConceptClass();
        SimpleObject ret = new SimpleObject();
        ret.put("description", ConversionUtil.getPropertyWithRepresentation(conceptClass, "description", Representation.DEFAULT));
        ret.put("name", ConversionUtil.getPropertyWithRepresentation(conceptClass, "name", Representation.DEFAULT));
        return ret;
    }

    @PropertyGetter("datatype")
    public SimpleObject getConceptDatatypeRepresentation(Concept instance) {
        ConceptDatatype conceptDatatype = instance.getDatatype();
        SimpleObject ret = new SimpleObject();
        ret.put("name", ConversionUtil.getPropertyWithRepresentation(conceptDatatype, "name", Representation.DEFAULT));
        ret.put("description", ConversionUtil.getPropertyWithRepresentation(conceptDatatype, "description", Representation.DEFAULT));
        ret.put("uuid", ConversionUtil.getPropertyWithRepresentation(conceptDatatype, "uuid", Representation.DEFAULT));
        return ret;
    }

    @PropertyGetter("name")
    public SimpleObject getNameRepresentation(Concept instance) {
        SimpleObject ret = new SimpleObject();
        ConceptName conceptName = instance.getName();
        ret.put("name", ConversionUtil.getPropertyWithRepresentation(conceptName, "name", Representation.DEFAULT));
        ret.put("conceptNameType", ConversionUtil.getPropertyWithRepresentation(conceptName, "conceptNameType", Representation.DEFAULT));
        ret.put("uuid", ConversionUtil.getPropertyWithRepresentation(conceptName, "uuid", Representation.DEFAULT));
        return ret;
    }

    @PropertyGetter("names")
    public Collection<ConceptName> getConceptNames(Concept instance){
        return instance.getNames();
    }
}
