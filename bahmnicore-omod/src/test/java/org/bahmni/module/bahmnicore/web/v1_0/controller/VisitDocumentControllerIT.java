package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmnicore.model.VisitDocumentResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.web.controller.BaseEmrControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;


@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class VisitDocumentControllerIT extends BaseEmrControllerTest {

    public static final String TMP_DOCUMENT_IMAGES = "/tmp/document_images";
    @Autowired
    private VisitService visitService;

    @Before
    public void setUp(){
        System.setProperty("bahmnicore.documents.baseDirectory", TMP_DOCUMENT_IMAGES);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(TMP_DOCUMENT_IMAGES));
    }

    @Test
    public void shouldCreateVisitEncounterAndObservation() throws Exception {
        executeDataSet("uploadDocuments.xml");
        String patientUUID = "75e04d42-3ca8-11e3-bf2b-0800271c1b75";
        String encounterTypeUUID ="759799ab-c9a5-435e-b671-77773ada74e4";
        String visitTypeUUID = "b45ca846-c79a-11e2-b0c0-8e397087571c";
        String testUUID = "e340cf44-3d3d-11e3-bf2b-0800271c1b75";
        String imageConceptUuid = "e060cf44-3d3d-11e3-bf2b-0800271c1b75";

        String json = "{" +
                    "\"patientUUID\":\"" + patientUUID + "\"," +
                    "\"visitTypeUUID\":\"" + visitTypeUUID + "\"," +
                    "\"visitStartDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"visitEndDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"encounterTypeUUID\":\"" + encounterTypeUUID + "\"," +
                    "\"encounterDateTime\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"documents\": [{\"testUUID\": \"" + testUUID + "\", \"image\": \"" + image + "\"}]" +
                "}";


        VisitDocumentResponse visitDocumentResponse = deserialize(handle(newPostRequest("/rest/v1/bahmnicore/visitDocument", json)), VisitDocumentResponse.class);
        Visit visit = visitService.getVisitByUuid(visitDocumentResponse.getVisitUuid());

        assertNotNull(visit);
        assertEquals(1, visit.getEncounters().size());
        Encounter encounters = new ArrayList<>(visit.getEncounters()).get(0);
        assertEquals(1, encounters.getAllObs().size());
        Obs parentObs = new ArrayList<>(encounters.getAllObs()).get(0);
        assertEquals(1, parentObs.getGroupMembers().size());
        assertObservationWithImage(parentObs, testUUID, imageConceptUuid);
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
    }

    private final String image = "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";
}
