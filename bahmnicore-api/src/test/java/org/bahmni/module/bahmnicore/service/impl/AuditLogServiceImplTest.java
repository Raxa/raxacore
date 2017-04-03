package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;
import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogResponse;
import org.bahmni.module.bahmnicore.dao.impl.AuditLogDaoImpl;
import org.bahmni.module.bahmnicore.model.AuditLog;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class AuditLogServiceImplTest {
    @InjectMocks
    private AuditLogServiceImpl auditLogService;
    @Mock
    private AuditLogDaoImpl auditLogDao;
    @Mock
    private Patient patient_1;
    @Mock
    private User user_1;
    @Mock
    private Patient patient_2;
    @Mock
    private User user_2;
    @Mock
    private PatientIdentifier patientIdentifier_1;
    @Mock
    private PatientIdentifier patientIdentifier_2;
    
    private Date dateCreated_1;
    private Date dateCreated_2;
    private ArrayList<AuditLog> mockAuditLogs;

    @Mock
    PatientService patientService;



    @Before
    public void setUp() throws Exception {
        dateCreated_1 = BahmniDateUtil.convertToDate("2017-03-15T16:57:09.0Z", BahmniDateUtil.DateFormatType.UTC);
        dateCreated_2 = BahmniDateUtil.convertToDate("2017-03-15T16:57:10.0Z", BahmniDateUtil.DateFormatType.UTC);
        when(patient_1.getPatientIdentifier()).thenReturn(patientIdentifier_1);
        when(patient_2.getPatientIdentifier()).thenReturn(patientIdentifier_2);
        mockAuditLogs = new ArrayList<>();
    }

    @Test
    public void getLogs_shouldGiveMappedAuditLogs() throws Exception {
        when(patientIdentifier_1.getIdentifier()).thenReturn("GAN2000");
        when(user_1.getUsername()).thenReturn("superman");
        when(patientIdentifier_2.getIdentifier()).thenReturn("GAN2001");
        when(user_2.getUsername()).thenReturn("batman");

        AuditLog auditLog_1 = new AuditLog();
        AuditLog auditLog_2 = new AuditLog();

        auditLog_1.setPatient(patient_1);
        auditLog_1.setMessage("message 1");
        auditLog_1.setUser(user_1);
        auditLog_1.setAuditLogId(1);
        auditLog_1.setDateCreated(dateCreated_1);
        auditLog_1.setEventType("event_type_1");
        auditLog_1.setUuid("uuid1");

        auditLog_2.setPatient(patient_2);
        auditLog_2.setMessage("message 2");
        auditLog_2.setUser(user_2);
        auditLog_2.setAuditLogId(2);
        auditLog_2.setDateCreated(dateCreated_2);
        auditLog_2.setEventType("event_type_2");
        auditLog_2.setUuid("uuid2");

        mockAuditLogs.add(auditLog_1);
        mockAuditLogs.add(auditLog_2);

        when(auditLogDao.getLogs("username", "patientId", null, 1,
                false, false)).thenReturn(mockAuditLogs);
        ArrayList<AuditLogResponse> logs = auditLogService.getLogs("username", "patientId",
                null, 1, false, false);
        assertEquals(2, logs.size());
        AuditLogResponse AuditLogResponse_1 = logs.get(0);
        AuditLogResponse AuditLogResponse_2 = logs.get(1);

        assertEquals("message 1", AuditLogResponse_1.getMessage());
        assertEquals("GAN2000", AuditLogResponse_1.getPatientId());
        assertEquals("superman", AuditLogResponse_1.getUserId());
        assertEquals("event_type_1", AuditLogResponse_1.getEventType());
        assertEquals(dateCreated_1, AuditLogResponse_1.getDateCreated());
        assertEquals(Integer.valueOf(1), AuditLogResponse_1.getAuditLogId());

        assertEquals("message 2", AuditLogResponse_2.getMessage());
        assertEquals("GAN2001", AuditLogResponse_2.getPatientId());
        assertEquals("batman", AuditLogResponse_2.getUserId());
        assertEquals("event_type_2", AuditLogResponse_2.getEventType());
        assertEquals(dateCreated_2, AuditLogResponse_2.getDateCreated());
        assertEquals(Integer.valueOf(2), AuditLogResponse_2.getAuditLogId());
    }
    @Test
    public void shouldCreateAuditLog() throws Exception {
        String patientUuid = "patientUuid";
        AuditLogPayload log = new AuditLogPayload(patientUuid, "message" ,"eventType");
        mockStatic(Context.class);
        User user = new User();
        user.setName("auditlogger");
        when(Context.getAuthenticatedUser()).thenReturn(user);
        when(Context.getPatientService()).thenReturn(patientService);
        Patient patient= new Patient();
        patient.setUuid(patientUuid);
        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);

        ArgumentCaptor<AuditLog> argument = ArgumentCaptor.forClass(AuditLog.class);

        auditLogService.createAuditLog(log);

        verify(auditLogDao).saveAuditLog(argument.capture());
        Assert.assertEquals(patientUuid,argument.getValue().getPatient().getUuid());
        Assert.assertEquals(log.getMessage(),argument.getValue().getMessage());
        Assert.assertEquals(log.getEventType(),argument.getValue().getEventType());

    }
}
