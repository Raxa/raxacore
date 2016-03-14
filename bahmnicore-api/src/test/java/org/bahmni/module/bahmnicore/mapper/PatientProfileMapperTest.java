package org.bahmni.module.bahmnicore.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;


@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class PatientProfileMapperTest {
    private SimpleObject propertiesToCreate;

    @Mock
    RestService restService;

    @Mock
    PatientResource1_8 patientResource1_8;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Patient patient = new Patient();
        patient.setGender("M");
        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getService(RestService.class)).thenReturn(restService);
        PowerMockito.when(restService.getResourceBySupportedClass(Patient.class)).thenReturn(patientResource1_8);
        PowerMockito.when(patientResource1_8.getPatient(any(SimpleObject.class))).thenReturn(patient);
        PowerMockito.when(patientResource1_8.getPatientForUpdate(anyString(), any(SimpleObject.class))).thenReturn(patient);
    }

    @Test
    public void should_map_for_create_patient() throws Exception {
        PatientProfileMapper patientProfileMapper = new PatientProfileMapper();
        SimpleObject propertiesToCreate = new SimpleObject();
        LinkedHashMap person = new LinkedHashMap();
        LinkedHashMap gender = new LinkedHashMap();
        gender.put("gender", "M");
        LinkedHashMap patient = new LinkedHashMap();
        ArrayList relationships = new ArrayList();
        patient.put("person", person);
        propertiesToCreate.put("patient", patient);
        propertiesToCreate.put("relationships", relationships);
        PatientProfile delegate = patientProfileMapper.mapForCreatePatient(propertiesToCreate);
        assertEquals(delegate.getPatient().getGender(), "M");
    }

    @Test
    public void should_map_for_update_patient() throws Exception {
        PatientProfileMapper patientProfileMapper = new PatientProfileMapper();
        SimpleObject propertiesToUpdate = new SimpleObject();
        LinkedHashMap person = new LinkedHashMap();
        LinkedHashMap gender = new LinkedHashMap();
        gender.put("gender", "M");
        LinkedHashMap patient = new LinkedHashMap();
        ArrayList relationships = new ArrayList();
        patient.put("person", person);
        propertiesToUpdate.put("patient", patient);
        propertiesToUpdate.put("relationships", relationships);
        String uuid = "";
        PatientProfile delegate = patientProfileMapper.mapForUpdatePatient(uuid, propertiesToUpdate);
        assertEquals(delegate.getPatient().getGender(), "M");
    }
}