package org.bahmni.module.admin.csv.service;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.bahmni.module.admin.csv.utils.CSVUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CSVPatientServiceTest {

    @Mock
    private PatientService mockPatientService;
    @Mock
    private AdministrationService mockAdminService;
    @Mock
    private AddressHierarchyService addressHierarchyService;
    @Mock
    private CSVAddressService csvAddressService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void save_patient_name() throws ParseException {
        PatientRow patientRow = new PatientRow();
        patientRow.firstName = "Romesh";
        patientRow.middleName = "Sharad";
        patientRow.lastName = "Powar";

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService, csvAddressService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Romesh", capturedPatient.getGivenName());
        assertEquals("Sharad", capturedPatient.getMiddleName());
        assertEquals("Powar", capturedPatient.getFamilyName());
    }

    @Test
    public void save_registrationNumber_birthdate_gender() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN);

        PatientRow patientRow = new PatientRow();
        patientRow.age = "34";
        patientRow.gender = "Male";
        patientRow.registrationNumber = "reg-no";
        patientRow.birthdate = "1998-07-07";

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService, csvAddressService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Male", capturedPatient.getGender());
        assertEquals("reg-no", capturedPatient.getPatientIdentifier().getIdentifier());
        assertEquals(simpleDateFormat.parse("1998-07-07") , capturedPatient.getBirthdate());

    }

    @Test
    public void save_registrationNumber_age_gender() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN);

        PatientRow patientRow = new PatientRow();
        patientRow.age = "34";
        patientRow.gender = "Male";
        patientRow.registrationNumber = "reg-no";


        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService, csvAddressService);

        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient capturedPatient = patientArgumentCaptor.getValue();
        assertEquals("Male", capturedPatient.getGender());
        assertEquals("reg-no", capturedPatient.getPatientIdentifier().getIdentifier());
        assertEquals(new Integer(34), capturedPatient.getAge());

    }

    @Test
    public void save_addressparts() throws ParseException {
        PatientRow  patientRow = new PatientRow();

        List<KeyValue> addressParts = new ArrayList<KeyValue>() {{
            add(new KeyValue("Cities", "zhumri tallayya"));
            add(new KeyValue("States", "Timbaktu"));
            add(new KeyValue("Countries", "Bharat"));
            add(new KeyValue("ZipCode", "555555"));
        }};
        patientRow.addressParts = addressParts;

        AddressHierarchyLevel cityLevel = new AddressHierarchyLevel();
        cityLevel.setName("Cities");
        cityLevel.setAddressField(AddressField.CITY_VILLAGE);

        AddressHierarchyLevel stateLevel = new AddressHierarchyLevel();
        stateLevel.setName("States");
        stateLevel.setAddressField(AddressField.STATE_PROVINCE);

        AddressHierarchyLevel countryLevel = new AddressHierarchyLevel();
        countryLevel.setName("Countries");
        countryLevel.setAddressField(AddressField.COUNTRY);

        AddressHierarchyLevel postalCodeLevel = new AddressHierarchyLevel();
        postalCodeLevel.setName("ZipCode");
        postalCodeLevel.setAddressField(AddressField.POSTAL_CODE);

        ArrayList<AddressHierarchyLevel> addressHierarchyLevels = new ArrayList<>();
        addressHierarchyLevels.add(cityLevel);
        addressHierarchyLevels.add(stateLevel);
        addressHierarchyLevels.add(countryLevel);
        addressHierarchyLevels.add(postalCodeLevel);

        when(addressHierarchyService.getAddressHierarchyLevels()).thenReturn(addressHierarchyLevels);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        CSVPatientService csvPatientService = new CSVPatientService(mockPatientService,mockAdminService, new CSVAddressService(addressHierarchyService));
        Patient savedPatient = csvPatientService.save(patientRow);

        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());

        Patient patient = patientArgumentCaptor.getValue();
        Set<PersonAddress> addresses = patient.getAddresses();
        PersonAddress capturedAddress = addresses.iterator().next();
        assertEquals("zhumri tallayya", capturedAddress.getCityVillage());
        assertEquals("Timbaktu", capturedAddress.getStateProvince());
        assertEquals("Bharat", capturedAddress.getCountry());
        assertEquals("555555", capturedAddress.getPostalCode());
    }
}