package org.bahmni.module.bahmnicore.web.v1_0.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

public class AuditLogControllerIT extends BaseIntegrationTest {
    @Autowired
    AuditLogController auditLogController;

    @Before
    public void setUp() throws Exception {
        executeDataSet("auditLogTestData.xml");
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFromGivenDate() throws Exception {
        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z")));

        JsonArray logs = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
        assertEquals(4, logs.size());
        JsonObject log_4 = logs.get(3).getAsJsonObject();
        assertEquals("BAH200001", log_4.get("patientId").getAsString());
        assertEquals("5", log_4.get("auditLogId").getAsString());
        assertEquals("VIEWED_CLINICAL", log_4.get("eventType").getAsString());
        assertEquals("thor", log_4.get("userId").getAsString());
        assertEquals("VIEWED_CLINICAL_DASHBOARD message", log_4.get("message").getAsString());

        JsonObject log_3 = logs.get(2).getAsJsonObject();
        assertEquals("GAN200000", log_3.get("patientId").getAsString());
        assertEquals("4", log_3.get("auditLogId").getAsString());
        assertEquals("VIEWED_DASHBOARD", log_3.get("eventType").getAsString());
        assertEquals("superuser", log_3.get("userId").getAsString());
        assertEquals("VIEWED_DASHBOARD message", log_3.get("message").getAsString());

        JsonObject log_2 = logs.get(1).getAsJsonObject();
        assertEquals("SEM200000", log_2.get("patientId").getAsString());
        assertEquals("3", log_2.get("auditLogId").getAsString());
        assertEquals("EDIT_CLINICAL", log_2.get("eventType").getAsString());
        assertEquals("spiderman", log_2.get("userId").getAsString());
        assertEquals("EDIT_CLINICAL message", log_2.get("message").getAsString());

        JsonObject log_1 = logs.get(0).getAsJsonObject();
        assertEquals("GAN200000", log_1.get("patientId").getAsString());
        assertEquals("1", log_1.get("auditLogId").getAsString());
        assertEquals("VIEWED_CLINICAL", log_1.get("eventType").getAsString());
        assertEquals("batman", log_1.get("userId").getAsString());
        assertEquals("VIEWED_CLINICAL_DASHBOARD message", log_1.get("message").getAsString());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsForDefaultView() throws Exception {
        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z"),
                new Parameter("defaultView", "true")));

        JsonArray logs = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
        assertEquals(4, logs.size());
        JsonObject log_1 = logs.get(0).getAsJsonObject();
        assertEquals("BAH200001", log_1.get("patientId").getAsString());
        assertEquals("5", log_1.get("auditLogId").getAsString());
        assertEquals("VIEWED_CLINICAL", log_1.get("eventType").getAsString());
        assertEquals("thor", log_1.get("userId").getAsString());
        assertEquals("VIEWED_CLINICAL_DASHBOARD message", log_1.get("message").getAsString());

        JsonObject log_2 = logs.get(1).getAsJsonObject();
        assertEquals("GAN200000", log_2.get("patientId").getAsString());
        assertEquals("4", log_2.get("auditLogId").getAsString());
        assertEquals("VIEWED_DASHBOARD", log_2.get("eventType").getAsString());
        assertEquals("superuser", log_2.get("userId").getAsString());
        assertEquals("VIEWED_DASHBOARD message", log_2.get("message").getAsString());

        JsonObject log_3 = logs.get(2).getAsJsonObject();
        assertEquals("SEM200000", log_3.get("patientId").getAsString());
        assertEquals("3", log_3.get("auditLogId").getAsString());
        assertEquals("EDIT_CLINICAL", log_3.get("eventType").getAsString());
        assertEquals("spiderman", log_3.get("userId").getAsString());
        assertEquals("EDIT_CLINICAL message", log_3.get("message").getAsString());

        JsonObject log_4 = logs.get(3).getAsJsonObject();
        assertEquals("GAN200000", log_4.get("patientId").getAsString());
        assertEquals("1", log_4.get("auditLogId").getAsString());
        assertEquals("VIEWED_CLINICAL", log_4.get("eventType").getAsString());
        assertEquals("batman", log_4.get("userId").getAsString());
        assertEquals("VIEWED_CLINICAL_DASHBOARD message", log_4.get("message").getAsString());

    }

    @Test
    public void createLog_shouldSaveLogInDatabase() throws Exception {
        String dataJson = "{\"eventType\":\"VIEWED_PATIENT_SEARCH\",\"message\":\"VIEWED_PATIENT_SEARCH_MESSAGE\"," +
                "\"module\":\"clinical\"}";
        MockHttpServletResponse response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z")));
        assertEquals(4, new JsonParser().parse(response.getContentAsString()).getAsJsonArray().size());
        handle(newPostRequest("/rest/v1/bahmnicore/auditlog", dataJson));
        response = handle(newGetRequest("/rest/v1/bahmnicore/auditlog",
                new Parameter("startFrom", "2017-03-12T16:57:09.0Z")));
        assertEquals(5, new JsonParser().parse(response.getContentAsString()).getAsJsonArray().size());
    }
}