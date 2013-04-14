package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.billing.BillingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.PatientService;

import javax.servlet.http.HttpServletResponse;

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
        controller = new BahmniPatientController(bahmniPatientService);
    }

    @Test
    public void dummyTestForFixingBuild() {
    }
}