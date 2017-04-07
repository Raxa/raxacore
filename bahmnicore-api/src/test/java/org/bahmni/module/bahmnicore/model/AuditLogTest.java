package org.bahmni.module.bahmnicore.model;

import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class AuditLogTest {
    @Mock
    private User user;
    @Mock
    private Patient patient;
    @Mock
    private PatientIdentifier patientIdentifier;

    private AuditLog auditLog;
    private Date dateCreated;

    @Before
    public void setUp() throws Exception {
        auditLog = new AuditLog();
        auditLog.setAuditLogId(1);
        dateCreated = BahmniDateUtil.convertToDate("2017-03-22T18:30:00.000Z",
                BahmniDateUtil.DateFormatType.UTC);
        auditLog.setDateCreated(dateCreated);
        auditLog.setEventType("EVENT_TYPE");
        auditLog.setMessage("dummy message");
        auditLog.setUuid("dummy uuid");
        auditLog.setUser(user);
        auditLog.setPatient(patient);
    }

    @Test
    public void map_shouldGiveAllDataAsSimpleObjectFormat() throws Exception {
        when(user.getUsername()).thenReturn("superman");
        when(patient.getPatientIdentifier()).thenReturn(patientIdentifier);
        when(patientIdentifier.getIdentifier()).thenReturn("GAN2001");

        SimpleObject responsePayload = auditLog.map();
        assertEquals(Integer.valueOf(1), responsePayload.get("auditLogId"));
        assertEquals(dateCreated, responsePayload.get("dateCreated"));
        assertEquals("EVENT_TYPE", responsePayload.get("eventType"));
        assertEquals("dummy message", responsePayload.get("message"));
        assertEquals("superman",responsePayload.get("userId"));
        assertEquals("GAN2001", responsePayload.get("patientId"));
    }

    @Test
    public void map_shouldGivePatientIdAsNullIfPatientIsNotPresent() throws Exception {
        when(user.getUsername()).thenReturn("superman");
        auditLog.setPatient(null);

        SimpleObject responsePayload = auditLog.map();
        assertEquals(Integer.valueOf(1), responsePayload.get("auditLogId"));
        assertEquals(dateCreated, responsePayload.get("dateCreated"));
        assertEquals("EVENT_TYPE", responsePayload.get("eventType"));
        assertEquals("dummy message", responsePayload.get("message"));
        assertEquals("superman",responsePayload.get("userId"));
        assertNull(responsePayload.get("patientId"));
    }

    @Test
    public void map_shouldGivePatientIdAsNullIfPatientDoesNotHaveIdentifier() throws Exception {
        when(user.getUsername()).thenReturn("superman");
        when(patient.getPatientIdentifier()).thenReturn(null);

        SimpleObject responsePayload = auditLog.map();
        assertEquals(Integer.valueOf(1), responsePayload.get("auditLogId"));
        assertEquals(dateCreated, responsePayload.get("dateCreated"));
        assertEquals("EVENT_TYPE", responsePayload.get("eventType"));
        assertEquals("dummy message", responsePayload.get("message"));
        assertEquals("superman",responsePayload.get("userId"));
        assertNull(responsePayload.get("patientId"));
    }

}