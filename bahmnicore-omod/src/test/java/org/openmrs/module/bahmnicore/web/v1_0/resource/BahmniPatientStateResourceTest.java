package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.User;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniPatientStateResourceTest extends BaseModuleWebContextSensitiveTest {

    @Test
    public void getAuditInfoShouldNotFailForPatientState() throws Exception {
        PatientProgram patientProgram = new PatientProgram();
        Date now = new Date();

        User user = new User();
        user.setUsername("Spider Man");
        user.getDisplayString();

        PatientState patientState = new PatientState();
        patientState.setDateCreated(now);
        patientState.setDateChanged(now);
        patientState.setCreator(user);
        patientState.setChangedBy(user);

        HashSet<PatientState> patientStates = new HashSet<>();
        patientStates.add(patientState);
        patientProgram.setStates(patientStates);

        BahmniPatientStateResource bahmniPatientStateResource = new BahmniPatientStateResource();
        SimpleObject actual = bahmniPatientStateResource.getAuditInfo(patientState);

        Assert.assertEquals(((SimpleObject)actual.get("creator")).get("username"),patientState.getCreator().getName());
        Assert.assertEquals(actual.get("dateCreated"),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(patientState.getDateCreated()));
        Assert.assertEquals(actual.get("dateChanged"),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(patientState.getDateChanged()));
        Assert.assertEquals(((SimpleObject)actual.get("changedBy")).get("username"),patientState.getChangedBy().getName());
    }
}