package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.openmrs.PatientState;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientStateResource1_8;

import java.lang.reflect.InvocationTargetException;



//TODO: Remove this class once we have  openmrs webservices 2.13 as the latest version has getAuditInfo in BaseDelegatingResource
@SubResource(parent = BahmniProgramEnrollmentResource.class, path = "state", supportedClass = PatientState.class, supportedOpenmrsVersions = { "1.12.*","2.*"}, order =0)
public class BahmniPatientStateResource extends PatientStateResource1_8 {

    /**
     * Need audit info in UI. Full representation of patient state is a very big object.
     * Hence we used custom representation to get audit info only.
     * This will be deprecated once we move to latest version of openmrs web services.
     * Gets the audit information of a resource.
     * @param resource the resource.
     * @return a {@link SimpleObject} with the audit information.
     */

    @PropertyGetter("auditInfo")
    public SimpleObject getAuditInfo(PatientState resource) throws InvocationTargetException, IllegalAccessException {
        SimpleObject ret = new SimpleObject();
        ret.put("creator", ConversionUtil.getPropertyWithRepresentation(resource, "creator", Representation.REF));
        ret.put("dateCreated", ConversionUtil.convertToRepresentation(resource.getDateCreated(), Representation.DEFAULT));
        ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(resource, "changedBy", Representation.REF));
        ret.put("dateChanged", ConversionUtil.convertToRepresentation(resource.getDateChanged(), Representation.DEFAULT));

        return ret;
    }

}
