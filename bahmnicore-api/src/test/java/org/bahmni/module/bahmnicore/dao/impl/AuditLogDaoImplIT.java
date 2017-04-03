package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.dao.AuditLogDao;
import org.bahmni.module.bahmnicore.model.AuditLog;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuditLogDaoImplIT extends BaseIntegrationTest {
    @Autowired
    AuditLogDao auditLogDao;

    @Autowired
    PatientService patientService;

    @Autowired
    UserService userService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("auditLogTestData.xml");
        AuditLogDaoImpl.LIMIT = 2;
    }

    @Test
    public void shouldGiveAllLogsCreatedAfterGivenDateTime() throws Exception {
        Date startDateTime = BahmniDateUtil.convertToDate("2017-03-15T16:57:09.0Z", BahmniDateUtil.DateFormatType.UTC);
        List<AuditLog> logs = auditLogDao.getLogs(null, null, startDateTime, null, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void shouldGivePreviousLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, 5, true, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void shouldGivePreviousLogsFromGivenIndexEvenIfSomeEventsAreDeletedInBetween() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, 4, true, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(1), auditLog_1.getAuditLogId());
        assertEquals("batman", auditLog_1.getUser().getUsername());
        assertEquals("GAN200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog_1.getUuid());

        assertEquals("EDIT_CLINICAL message", auditLog_2.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_2.getAuditLogId());
        assertEquals("spiderman", auditLog_2.getUser().getUsername());
        assertEquals("SEM200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_2.getUuid());

    }

    @Test
    public void shouldGiveNextLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, 2, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals("spiderman", auditLog_1.getUser().getUsername());
        assertEquals("SEM200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void getLogs_shouldGiveLogsInDescendingIfItIsDefaultView() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, false, true);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(5), auditLog_1.getAuditLogId());
        assertEquals("thor", auditLog_1.getUser().getUsername());
        assertEquals("BAH200001", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87e", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFilterByGivenUsername() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs("batman", null, null, null, false, false);
        assertEquals(1, logs.size());
        AuditLog auditLog = logs.get(0);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog.getEventType());
        assertEquals(Integer.valueOf(1), auditLog.getAuditLogId());
        assertEquals("batman", auditLog.getUser().getUsername());
        assertEquals("GAN200000", auditLog.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog.getUuid());
    }

    @Test
    public void getLogs_shouldGiveAuditLogsFilterByGivenPatientId() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, "GAN200000", null, null, false, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(1), auditLog_1.getAuditLogId());
        assertEquals("batman", auditLog_1.getUser().getUsername());
        assertEquals("GAN200000", auditLog_1.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb67a", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals("superuser", auditLog_2.getUser().getUsername());
        assertEquals("GAN200000", auditLog_2.getPatient().getPatientIdentifier().getIdentifier());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void getLogs_shouldGiveEmptyListIfTheGivePatientIdentifierIsInvalid() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, "GAN200100", null, null, false, false);
        assertEquals(0, logs.size());
    }

    @Test
    public void getLogs_shouldGiveEmptyListIfTheGiveUsernameIsInvalid() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs("antman", "GAN200000", null, null, false, false);
        assertEquals(0, logs.size());
    }


    @Test
    public void shouldPersistLogs() throws Exception {
        AuditLog auditLog = new AuditLog();
        auditLog.setUuid("uuid");
        auditLog.setMessage("message");
        Patient patient = patientService.getPatientByUuid("75e04d42-3ca8-11e3-bf2b-0800271c1b81");
        auditLog.setPatient(patient);
        User user = userService.getUserByUuid("Z9fd3a7b-6482-487d-87eR-c07b123MxF99");
        auditLog.setUser(user);
        auditLog.setEventType("event1");
        Date now = new Date();
        auditLog.setDateCreated(now);

        int countBefore = auditLogDao.getLogs(user.getUsername(), "SEM200000",null,null,false,false).size();
        auditLogDao.saveAuditLog(auditLog);
        int countAfter = auditLogDao.getLogs(user.getUsername(), "SEM200000", null, null, false, false).size();
        assertEquals(1, countAfter-countBefore);
    }
}