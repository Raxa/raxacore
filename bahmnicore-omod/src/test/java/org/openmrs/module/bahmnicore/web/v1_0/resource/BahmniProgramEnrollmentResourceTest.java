package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.Map;
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniProgramEnrollmentResourceTest {

    BahmniProgramEnrollmentResource bahmniProgramEnrollmentResource;

    @Test
    public void testRepresentationDescription() throws Exception{
        bahmniProgramEnrollmentResource = new BahmniProgramEnrollmentResource();
        DelegatingResourceDescription delegatingResourceDescription = bahmniProgramEnrollmentResource.getRepresentationDescription(Representation.FULL);
        Map<String, DelegatingResourceDescription.Property> properties = delegatingResourceDescription.getProperties();
        Assert.assertTrue(properties.containsKey("attributes"));
        Assert.assertEquals(properties.get("attributes").getRep(),Representation.DEFAULT);
        Assert.assertTrue(properties.containsKey("states"));
        Assert.assertEquals(properties.get("states").getRep().getRepresentation(),"(auditInfo,uuid,startDate,endDate,voided,state:REF)");

    }
}