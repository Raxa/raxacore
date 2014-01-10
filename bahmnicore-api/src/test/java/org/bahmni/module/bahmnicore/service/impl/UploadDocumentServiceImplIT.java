package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.model.Document;
import org.bahmni.module.bahmnicore.model.VisitDocumentUpload;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.service.UploadDocumentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UploadDocumentServiceImplIT extends BaseModuleWebContextSensitiveTest {

    private UploadDocumentService uploadDocumentService ;

    @Mock
    private PatientImageService patientImageService;

    @Before
    public void setup() throws Exception {
        executeDataSet("uploadDocuments.xml");
        initMocks(this);
        when(patientImageService.saveDocument(anyInt(),anyString(),anyString())).thenReturn("url1");
        uploadDocumentService = new UploadDocumentServiceImpl(patientImageService);
    }

    @Test
    public void shouldCreateVisitEncounterAndObservation() {

        String patientUUID = "75e04d42-3ca8-11e3-bf2b-0800271c1b75";
        String encounterTypeUUID ="759799ab-c9a5-435e-b671-77773ada74e4";
        Date encounterDateTime = new Date(2014, 1, 8, 0, 0, 0);
        String visitTypeUUID = "b45ca846-c79a-11e2-b0c0-8e397087571c";
        Date visitStartDate = new Date(2014, 1, 8, 0, 0, 0);
        Date visitEndDate = new Date(2014, 1, 9, 0, 0, 0);

        String testUUID = "e340cf44-3d3d-11e3-bf2b-0800271c1b75";
        String documentUUID = "e060cf44-3d3d-11e3-bf2b-0800271c1b75";
        String image1 = "image1";
        Document document1 = new Document(image1, testUUID);
        String image2 = "image2";
        Document document2 = new Document(image2, testUUID);
        List<Document> documents = new ArrayList<>(Arrays.asList(document1, document2));

        VisitDocumentUpload visitDocumentUpload = new VisitDocumentUpload(patientUUID,visitTypeUUID, visitStartDate, visitEndDate, encounterTypeUUID, encounterDateTime, documents);
        Visit visit = uploadDocumentService.upload(visitDocumentUpload);
        assertTrue(visit != null);
        assertTrue(visit.getEncounters().size() == 1);
        Encounter encounters = new ArrayList<>(visit.getEncounters()).get(0);
        assertTrue(encounters.getAllObs().size() == 1);
        Obs parentObs = new ArrayList<>(encounters.getAllObs()).get(0);
        assertEquals(2, parentObs.getGroupMembers().size());
        assertObservationWithImage(parentObs, testUUID, documentUUID);
    }

    private void assertObservationWithImage(Obs parentObs, String testUUID, String documentUUID) {
        Obs expectedObservation = null;
        assertEquals(parentObs.getConcept().getUuid(),testUUID);
        assertTrue(parentObs.getGroupMembers().size() > 0);
        for (Obs memberObs : parentObs.getGroupMembers()) {
            if(documentUUID.equals(memberObs.getConcept().getUuid())) {
                expectedObservation = memberObs;
                break;
            }
        }
        assertTrue(expectedObservation != null);
        verify(patientImageService, times(2)).saveDocument(anyInt(),anyString(), anyString());
    }

}
