package org.bahmni.module.admin.csv.service;

import org.bahmni.module.admin.csv.models.PatientRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CSVPatientServiceTest {

    @Mock
    private PatientService mockPatientService;
    @Mock
    private AdministrationService mockAdminService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void save_patient_name() throws ParseException {
        PatientRow patientRow = new PatientRow();
        patientRow.setFirstName("Romesh");
        patientRow.setMiddleName("Sharad");
        patientRow.setLastName("Powar");

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Romesh", capturedPatient.getGivenName());
        assertEquals("Sharad", capturedPatient.getMiddleName());
        assertEquals("Powar", capturedPatient.getFamilyName());
    }

    @Test
    public void save_registrationNumber_birthdate_gender() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");

        PatientRow patientRow = new PatientRow();
        patientRow.setAge("34");
        patientRow.setGender("Male");
        patientRow.setRegistrationNumber("reg-no");
        patientRow.setBirthdate("1998-07-07");


        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Male", capturedPatient.getGender());
        assertEquals("reg-no", capturedPatient.getPatientIdentifier().getIdentifier());
        assertEquals(simpleDateFormat.parse("1998-07-07") , capturedPatient.getBirthdate());

    }

    @Test
    public void save_registrationNumber_age_gender() throws ParseException {
        PatientRow patientRow = new PatientRow();
        patientRow.setAge("34");
        patientRow.setGender("Male");
        patientRow.setRegistrationNumber("reg-no");


        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Male", capturedPatient.getGender());
        assertEquals("reg-no", capturedPatient.getPatientIdentifier().getIdentifier());
        assertEquals(new Integer(34), capturedPatient.getAge());

    }

}