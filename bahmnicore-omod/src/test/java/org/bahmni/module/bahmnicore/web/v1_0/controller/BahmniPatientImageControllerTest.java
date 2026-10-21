package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.PatientDocumentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class BahmniPatientImageControllerTest {

    private BahmniPatientImageController bahmniPatientImageController;

    @Mock
    private PatientDocumentService patientDocumentService;

    @Mock
    private UserContext userContext;

    private MockedStatic<Context> contextMockedStatic;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        contextMockedStatic = Mockito.mockStatic(Context.class);
        contextMockedStatic.when(Context::getUserContext).thenReturn(userContext);
        bahmniPatientImageController = new BahmniPatientImageController(patientDocumentService);
    }

    @After
    public void tearDown() {
        contextMockedStatic.close();
    }

    @Test
    public void shouldRespondWithFileNotFoundStatusCodeIfTheImageIsNotFound() throws Exception {
        Mockito.when(userContext.isAuthenticated()).thenReturn(true);
        when(patientDocumentService.retriveImage(anyString())).thenReturn(new ResponseEntity<Object>(new Object(), HttpStatus.OK));
        String patientUuid = "patientUuid";

        ResponseEntity<Object> responseEntity = bahmniPatientImageController.getImage(patientUuid);

        verify(patientDocumentService).retriveImage(patientUuid);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void shouldRespondWithNotAuthorizeStatusCodeIfTheImageIsNotFound() throws Exception {
        Mockito.when(userContext.isAuthenticated()).thenReturn(false);
        when(patientDocumentService.retriveImage(anyString())).thenReturn(new ResponseEntity<Object>(new Object(), HttpStatus.OK));
        String patientUuid = "patientUuid";

        ResponseEntity<Object> responseEntity = bahmniPatientImageController.getImage(patientUuid);

        verify(patientDocumentService, never()).retriveImage(patientUuid);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}