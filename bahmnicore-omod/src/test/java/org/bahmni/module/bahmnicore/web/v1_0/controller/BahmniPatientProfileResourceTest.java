package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.EmrPatientProfileService;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest({Context.class, BahmniPatientProfileResource.class})
@RunWith(PowerMockRunner.class)
public class BahmniPatientProfileResourceTest {

    @Mock
    private EmrPatientProfileService emrPatientProfileService;

    @Mock
    private RestService restService;

    @Mock
    PatientResource1_8 patientResource1_8;

    @Mock
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;
    private BahmniPatientProfileResource bahmniPatientProfileResource;
    private SimpleObject propertiesToCreate;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("patient.json").getFile());
        String jsonString = FileUtils.readFileToString(file);
        propertiesToCreate = new SimpleObject().parseJson(jsonString);
        Patient patient = new Patient();
        patient.setGender("M");
        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getService(RestService.class)).thenReturn(restService);
        PowerMockito.when(restService.getResourceBySupportedClass(Patient.class)).thenReturn(patientResource1_8);
        PowerMockito.when(patientResource1_8.getPatient(any(SimpleObject.class))).thenReturn(patient);
        PowerMockito.when(patientResource1_8.getPatientForUpdate(anyString(), any(SimpleObject.class))).thenReturn(patient);
    }

    @Test
    public void createPatient() throws Exception {
        bahmniPatientProfileResource = new BahmniPatientProfileResource(emrPatientProfileService, identifierSourceServiceWrapper);
        BahmniPatientProfileResource spy = spy(bahmniPatientProfileResource);
        PatientProfile delegate = new PatientProfile();
        when(identifierSourceServiceWrapper.generateIdentifier("BAH", "")).thenReturn("BAH300010");
        PowerMockito.doReturn(delegate).when(spy, "mapForCreatePatient", propertiesToCreate);
        when(emrPatientProfileService.save(delegate)).thenReturn(delegate);
        doNothing().when(spy).setConvertedProperties(any(PatientProfile.class), any(SimpleObject.class), any(DelegatingResourceDescription.class), any(Boolean.class));
        ResponseEntity<Object> response = spy.create(false, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());

    }

    @Test
    public void updatePatient() throws Exception {
        bahmniPatientProfileResource = new BahmniPatientProfileResource(emrPatientProfileService, identifierSourceServiceWrapper);
        BahmniPatientProfileResource spy = spy(bahmniPatientProfileResource);
        PatientProfile delegate = new PatientProfile();
        when(identifierSourceServiceWrapper.generateIdentifier("BAH", "")).thenReturn("BAH300010");
        PowerMockito.doReturn(delegate).when(spy, "mapForUpdatePatient", anyString(), any(SimpleObject.class));
        when(emrPatientProfileService.save(delegate)).thenReturn(delegate);
        doNothing().when(spy).setConvertedProperties(any(PatientProfile.class), any(SimpleObject.class), any(DelegatingResourceDescription.class), any(Boolean.class));
        ResponseEntity<Object> response = spy.update("someUuid", propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());

    }

}