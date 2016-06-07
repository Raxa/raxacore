package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.EmrPatientProfileService;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.idgen.contract.IdentifierSource;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
public class BahmniPatientProfileResourceIT extends BaseIntegrationTest {

    @Autowired
    private EmrPatientProfileService emrPatientProfileService;

    private BahmniPatientProfileResource bahmniPatientProfileResource;
    private SimpleObject propertiesToCreate;
    private ClassLoader classLoader;

    @Mock
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    @Mock
    private IdentifierSource identifierSource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        executeDataSet("createPatientMetadata.xml");
        bahmniPatientProfileResource = new BahmniPatientProfileResource(emrPatientProfileService, identifierSourceServiceWrapper);
        when(identifierSourceServiceWrapper.getSequenceValueUsingIdentifierSourceUuid("dead-cafe")).thenReturn("300010");
        when(identifierSourceServiceWrapper.generateIdentifierUsingIdentifierSourceUuid("dead-cafe", "")).thenReturn("BAH300010");
        classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("patient.json").getFile());
        String jsonString = FileUtils.readFileToString(file);
        propertiesToCreate = new SimpleObject().parseJson(jsonString);
    }

    @Test
    public void shouldReturnHttpPreconditionFailedStatusAndJumpSizeIfIdentifierIsPassedInTheRequestAndTheirIsAJump() throws Exception {
        LinkedHashMap identifierProperties = (LinkedHashMap) ((ArrayList) ((LinkedHashMap) propertiesToCreate.get("patient")).get("identifiers")).get(0);
        String identifier = String.valueOf(identifierProperties.get("identifierPrefix")).concat("300020");
        identifierProperties.put("identifier", identifier);
        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);
        SimpleObject simpleObject = new SimpleObject();
        simpleObject = simpleObject.parseJson(String.valueOf(response.getBody()));
        Assert.assertEquals(412, response.getStatusCode().value());
        Assert.assertEquals(10, Integer.parseInt(String.valueOf(simpleObject.get("sizeOfJump"))));
    }

    @Test
    public void shouldCreatePatientWhenUserAcceptsTheJump() throws Exception {
        LinkedHashMap identifierProperties = (LinkedHashMap) ((ArrayList) ((LinkedHashMap) propertiesToCreate.get("patient")).get("identifiers")).get(0);
        String identifier = String.valueOf(identifierProperties.get("identifierPrefix")).concat("300020");
        identifierProperties.put("identifier", identifier);
        ResponseEntity<Object> response = bahmniPatientProfileResource.create(true, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void shouldCreatePatientWhenIdentifierIsPassedAndJumpIsZero() throws Exception {
        LinkedHashMap identifierProperties = (LinkedHashMap) ((ArrayList) ((LinkedHashMap) propertiesToCreate.get("patient")).get("identifiers")).get(0);
        String identifier = String.valueOf(identifierProperties.get("identifierPrefix")).concat("300010");
        identifierProperties.put("identifier", identifier);
        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void shouldCreatePatientWhenIdentifierPrefixIsNotPresentAndIdentifierIsManuallyEntered() throws Exception {
        HashMap<String, Object> patient = propertiesToCreate.get("patient");
        List<HashMap<String, String>> identifiers = (ArrayList<HashMap<String, String>>) patient.get("identifiers");
        identifiers.get(0).put("identifier", "identifier");
        identifiers.get(0).put("identifierPrefix", "");

        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);

        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void shouldCreatePatientWhenIdentifierPrefixIsBlankAndNoIdentifierIsEntered() throws Exception {
        when(identifierSourceServiceWrapper.getAllIdentifierSources()).thenReturn(Arrays.asList(identifierSource));
        when(identifierSource.getName()).thenReturn("identifierName");
        when(identifierSourceServiceWrapper.generateIdentifier("identifierName", "")).thenReturn("300010");
        HashMap<String, Object> patient = propertiesToCreate.get("patient");
        List<HashMap<String, String>> identifiers = (ArrayList<HashMap<String, String>>) patient.get("identifiers");
        identifiers.get(0).put("identifierPrefix", "");

        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);

        Assert.assertEquals(200, response.getStatusCode().value());
    }
    
    @Test
    public void shouldReturnBadRequestForInvalidJson() throws Exception {
        LinkedHashMap person = ((LinkedHashMap)((LinkedHashMap)propertiesToCreate.get("patient")).get("person"));
        person.remove("names");
        ResponseEntity<Object> response = bahmniPatientProfileResource.create(false, propertiesToCreate);
        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void shouldUpdatePatient() throws Exception {
        File file = new File(classLoader.getResource("updatePatient.json").getFile());
        String jsonString = FileUtils.readFileToString(file);
        propertiesToCreate = new SimpleObject().parseJson(jsonString);
        String uuid = "592b29e1-b3f5-423e-83cb-0d2c9b80867f";
        ResponseEntity<Object> response = bahmniPatientProfileResource.update(uuid, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals("Wed Mar 07 00:00:00 IST 1984", ((PatientProfile) response.getBody()).getPatient().getBirthdate().toString());
    }

    @Test
    public void shouldReturnBadRequestForLongPatientName() throws Exception {
        File file = new File(classLoader.getResource("updatePatient.json").getFile());
        String jsonString = FileUtils.readFileToString(file);
        propertiesToCreate = new SimpleObject().parseJson(jsonString);
        LinkedHashMap name = (LinkedHashMap) ((ArrayList) ((LinkedHashMap) ((LinkedHashMap) propertiesToCreate.get("patient")).get("person")).get("names")).get(0);
        name.put("givenName", "LongStringLongStringLongStringLongStringLongStringLongString");
        String uuid = "592b29e1-b3f5-423e-83cb-0d2c9b80867f";
        ResponseEntity<Object> response = bahmniPatientProfileResource.update(uuid, propertiesToCreate);
        Assert.assertEquals(400, response.getStatusCode().value());
    }
}