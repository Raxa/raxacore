package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AuditLogControllerIT extends BaseIntegrationTest {
    @Autowired
    AuditLogController auditLogController;

    private String expected;

    @Before
    public void setUp() throws Exception {
        executeDataSet("auditLogTestData.xml");
        expected = "[{\"patientId\":\"GAN200000\",\"auditLogId\":1,\"dateCreated\":1489573629000," +
                "\"eventType\":\"VIEWED_CLINICAL\",\"userId\":\"batman\",\"message\":\"VIEWED_CLINICAL_DASHBOARD " +
                "message\"},{\"patientId\":\"SEM200000\",\"auditLogId\":3,\"dateCreated\":1489577230000,\"eventType\"" +
                ":\"EDIT_CLINICAL\",\"userId\":\"spiderman\",\"message\":\"EDIT_CLINICAL message\"},{\"patientId\":" +
                "\"GAN200000\",\"auditLogId\":4,\"dateCreated\":1489577290000,\"eventType\":\"VIEWED_DASHBOARD\"," +
                "\"userId\":\"superuser\",\"message\":\"VIEWED_DASHBOARD message\"},{\"patientId\":\"BAH200001\"," +
                "\"auditLogId\":5,\"dateCreated\":1489577350000,\"eventType\":\"VIEWED_CLINICAL\",\"userId\":\"thor\"" +
                ",\"message\":\"VIEWED_CLINICAL_DASHBOARD message\"}]";
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFromGivenDate() throws Exception {
        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z")));
        assertEquals(expected, response.getContentAsString());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsForDefaultView() throws Exception {
        String expected = "[{\"patientId\":\"BAH200001\",\"auditLogId\":5,\"dateCreated\":1489577350000,\"eventType\"" +
                ":\"VIEWED_CLINICAL\",\"userId\":\"thor\",\"message\":\"VIEWED_CLINICAL_DASHBOARD message\"}," +
                "{\"patientId\":\"GAN200000\",\"auditLogId\":4,\"dateCreated\":1489577290000,\"eventType\":" +
                "\"VIEWED_DASHBOARD\",\"userId\":\"superuser\",\"message\":\"VIEWED_DASHBOARD message\"}," +
                "{\"patientId\":\"SEM200000\",\"auditLogId\":3,\"dateCreated\":1489577230000,\"eventType\":" +
                "\"EDIT_CLINICAL\",\"userId\":\"spiderman\",\"message\":\"EDIT_CLINICAL message\"},{\"patientId\"" +
                ":\"GAN200000\",\"auditLogId\":1,\"dateCreated\":1489573629000,\"eventType\":\"VIEWED_CLINICAL\"," +
                "\"userId\":\"batman\",\"message\":\"VIEWED_CLINICAL_DASHBOARD message\"}]";

        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z"),
                new Parameter("defaultView", "true")));

        assertEquals(expected, response.getContentAsString());
    }

    @Test
    public void createLog_shouldSaveLogInDatabase() throws Exception {
        String dataJson = "{\"eventType\":\"VIEWED_PATIENT_SEARCH\",\"message\":\"VIEWED_PATIENT_SEARCH_MESSAGE\"," +
                "\"module\":\"clinical\"}";
        handle(newPostRequest("/rest/v1/bahmnicore/auditlog", dataJson));
        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z")));
        assertTrue(response.getContentAsString().contains("\"eventType\":\"VIEWED_PATIENT_SEARCH\",\"userId\":" +
                "\"admin\",\"message\":\"VIEWED_PATIENT_SEARCH_MESSAGE\""));
        assertNotEquals(expected, response.getContentAsString());
    }
}