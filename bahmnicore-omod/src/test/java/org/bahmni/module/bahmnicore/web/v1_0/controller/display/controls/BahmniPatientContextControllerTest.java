package org.bahmni.module.bahmnicore.web.v1_0.controller.display.controls;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.BahmniPatientContextMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.patient.PatientContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class BahmniPatientContextControllerTest {

    @InjectMocks
    private BahmniPatientContextController bahmniPatientContextController = new BahmniPatientContextController();

    @Mock
    private PatientService patientService;

    @Mock
    private BahmniProgramWorkflowService bahmniProgramWorkflowService;

    @Mock
    private BahmniPatientContextMapper bahmniPatientContextMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(BahmniProgramWorkflowService.class)).thenReturn(bahmniProgramWorkflowService);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldGetCorePersonInformationIfPersonAttributesAndProgramAttributesAreNotConfigured() {
        String patientUuid = "patientUuid";
        String programUuid = "programUuid";
        Patient patient = new Patient();
        PatientContext expectedPatientContext = new PatientContext();
        List<String> configuredPersonAttributes = Collections.singletonList("Caste");
        List<String> configuredProgramAttributes = Collections.singletonList("IRDB Number");
        BahmniPatientProgram bahmniPatientProgram = new BahmniPatientProgram();

        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        when(bahmniPatientContextMapper.map(patient, bahmniPatientProgram, configuredPersonAttributes, configuredProgramAttributes)).thenReturn(expectedPatientContext);
        when(bahmniProgramWorkflowService.getPatientProgramByUuid(programUuid)).thenReturn(bahmniPatientProgram);

        PatientContext actualPatientContext = bahmniPatientContextController.getPatientContext(patientUuid, programUuid, configuredPersonAttributes, configuredProgramAttributes);

        verify(patientService, times(1)).getPatientByUuid(patientUuid);
        verify(bahmniPatientContextMapper, times(1)).map(patient, bahmniPatientProgram, configuredPersonAttributes, configuredProgramAttributes);
        verify(bahmniProgramWorkflowService, times(1)).getPatientProgramByUuid(programUuid);
        assertEquals(expectedPatientContext, actualPatientContext);
    }
}