package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.util.PatientMother;
import org.bahmni.module.billing.BillingService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RaxaPatientControllerTest extends BaseModuleContextSensitiveTest {
    @Mock
    private PatientService patientService;
    @Mock
    private BillingService billingService;
    @Mock
    private HttpServletResponse response;
    private RaxaPatientController controller;

    @Before
    public void setup() {
        initMocks(this);
        controller = new RaxaPatientController(billingService);
        controller.setPatientService(patientService);
    }

    @Test
    @Ignore
    public void shouldCallMapToExistingPatient() throws ResponseException {
        SimpleObject firstPatientToSave = new PatientMother().buildSimpleObject();

        controller.createNewPatient(firstPatientToSave, null, null);
    }

    @Test
    public void shouldCallOpenErpServiceAfterPatientSave() throws Exception {
        String identifier = "BAH420420";
        PatientMother patientMother = new PatientMother().withName("ram", "boo", "singh").withPatientIdentifier(identifier);
        when(patientService.savePatient(any(Patient.class))).thenReturn(patientMother.build());
        SimpleObject post = patientMother.buildSimpleObject();

        controller.createNewPatient(post, null, response);

        verify(billingService).createCustomer("ram boo singh", identifier);
    }

    @Test
    public void shouldNotCallOpenErpServiceWhenPatienIsNotSavedForAnyReason() throws Exception {
        when(patientService.savePatient(any(Patient.class))).thenThrow(new DAOException("Some error!"));

        try {
            controller.createNewPatient(new PatientMother().buildSimpleObject(), null, response);
        } catch (DAOException e) {

        }

        verify(billingService, never()).createCustomer(anyString(), anyString());
    }
}
