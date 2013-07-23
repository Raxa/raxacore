package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.BahmniCoreException;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.util.PatientMother;
import org.bahmni.module.billing.BillingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.db.DAOException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniPatientServiceImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniPatientServiceImpl bahmniPatientService;

    @Before
    public void setup() {
    }

    @Test
    public void shouldSearchByPatientIdentifier() throws Exception {
//        List<Patient> patients = bahmniPatientService.search("XYZ", null, null);
//        assertEquals(1, patients.size());
    }


}
