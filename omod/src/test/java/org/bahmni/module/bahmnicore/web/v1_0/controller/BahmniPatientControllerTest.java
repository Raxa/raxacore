package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.bahmnicore.util.PatientMother;
import org.bahmni.module.billing.BillingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class BahmniPatientControllerTest {
    @Mock
    private PatientService patientService;
    @Mock
    private BillingService billingService;
    @Mock
    private BahmniPatientService bahmniPatientService;
    @Mock
    private HttpServletResponse response;

    @Mock
    private PatientMapper patientMapper;

    private BahmniPatientController controller;

    @Before
    public void setup() {
        initMocks(this);
        controller = new BahmniPatientController(billingService);
        controller.setPatientService(patientService);
        controller.setPatientMapper(patientMapper);
    }

    @Test
    public void shouldMapPostValuesToNewPatientOnCreate() throws ResponseException {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        SimpleObject post = patientMother.buildSimpleObject();

        controller.createNewPatient(post, null, response);

        verify(patientMapper).map(Matchers.<Patient>eq(null), any(BahmniPatient.class));
    }

    @Test
    public void shouldMapPostValuesToExistingPatientOnUpdate() throws ResponseException {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        String uuid = "a23034-asdf954-asdfasdf-342343";
        Patient patient = patientMother.build();
        when(patientService.getPatientByUuid(uuid)).thenReturn(patient);
        SimpleObject post = patientMother.buildSimpleObject();

        controller.updatePatient(uuid, post, null, response);

        verify(patientMapper).map(eq(patient), any(BahmniPatient.class));
    }

    @Test
    public void shouldSaveMappedPatientOnCreate() throws ResponseException {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        Patient patient = patientMother.build();
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patient);
        when(patientService.savePatient(eq(patient))).thenReturn(patientMother.build());
        SimpleObject post = patientMother.buildSimpleObject();

        controller.createNewPatient(post, null, response);

        verify(patientService).savePatient(patient);
    }

    @Test
    public void shouldSaveMappedPatientOnUpdate() throws ResponseException {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        SimpleObject post = patientMother.buildSimpleObject();
        String uuid = "a23034-asdf954-asdfasdf-342343";
        Patient patient = patientMother.build();
        when(patientService.getPatientByUuid(uuid)).thenReturn(patient);
        Patient mappedPatient = patientMother.build();
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(mappedPatient);
        when(patientService.savePatient(eq(mappedPatient))).thenReturn(patientMother.build());

        controller.updatePatient(uuid, post, null, response);

        verify(patientService).savePatient(mappedPatient);
    }

    @Test
    public void shouldCallOpenErpServiceAfterPatientSave() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        SimpleObject post = patientMother.buildSimpleObject();

        controller.createNewPatient(post, null, response);

        verify(billingService).createCustomer("ram boo singh", identifier);
    }

    @Test
    public void shouldNotCallOpenErpServiceWhenUpdatingPatient() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        SimpleObject post = patientMother.buildSimpleObject();

        controller.updatePatient("000111-939ddee-93diddd-99dj32d-9219dk", post, null, response);

        verify(billingService, never()).createCustomer(anyString(), anyString());
    }

    @Test
    public void shouldNotCallOpenErpServiceWhenPatienIsNotSavedForAnyReason() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientMapper.map(any(Patient.class), any(BahmniPatient.class))).thenReturn(patientMother.build());
        when(patientService.savePatient(any(Patient.class))).thenThrow(new DAOException("Some error!"));

        try {
            controller.createNewPatient(new PatientMother().buildSimpleObject(), null, response);
        } catch (DAOException e) {

        }
        verify(billingService, never()).createCustomer(anyString(), anyString());
    }
}
