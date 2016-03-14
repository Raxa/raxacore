package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmnicore.mapper.PatientProfileMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.emrapi.patient.EmrPatientProfileService;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.idgen.webservices.services.IdentifierSourceServiceWrapper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

public class BahmniPatientProfileResourceTest {

    @Mock
    private PatientProfileMapper patientProfileMapper;

    @Mock
    private EmrPatientProfileService emrPatientProfileService;

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
    }

    @Test
    public void createPatient() throws Exception {
        bahmniPatientProfileResource = new BahmniPatientProfileResource(patientProfileMapper, emrPatientProfileService, identifierSourceServiceWrapper);
        BahmniPatientProfileResource spy = spy(bahmniPatientProfileResource);
        PatientProfile delegate = new PatientProfile();
        when(identifierSourceServiceWrapper.generateIdentifier("BAH", "")).thenReturn("BAH300010");
        when(patientProfileMapper.mapForCreatePatient(propertiesToCreate)).thenReturn(new PatientProfile());
        when(emrPatientProfileService.save(delegate)).thenReturn(delegate);
        doNothing().when(spy).setConvertedProperties(any(PatientProfile.class), any(SimpleObject.class), any(DelegatingResourceDescription.class), any(Boolean.class));
        ResponseEntity<Object> response = spy.create(false, propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());

    }

    @Test
    public void updatePatient() throws Exception {
        bahmniPatientProfileResource = new BahmniPatientProfileResource(patientProfileMapper, emrPatientProfileService, identifierSourceServiceWrapper);
        BahmniPatientProfileResource spy = spy(bahmniPatientProfileResource);
        PatientProfile delegate = new PatientProfile();
        when(identifierSourceServiceWrapper.generateIdentifier("BAH", "")).thenReturn("BAH300010");
        when(patientProfileMapper.mapForUpdatePatient("someUuid",propertiesToCreate)).thenReturn(new PatientProfile());
        when(emrPatientProfileService.save(delegate)).thenReturn(delegate);
        doNothing().when(spy).setConvertedProperties(any(PatientProfile.class), any(SimpleObject.class), any(DelegatingResourceDescription.class), any(Boolean.class));
        ResponseEntity<Object> response = spy.update("someUuid", propertiesToCreate);
        Assert.assertEquals(200, response.getStatusCode().value());

    }

}