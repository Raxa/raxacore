package org.bahmni.module.admin.auditLog.dao.impl;

import org.bahmni.module.admin.auditLog.dao.AuditLogDao;
import org.bahmni.module.admin.auditLog.model.AuditLog;
import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuditLogDaoImplIT extends BaseIntegrationTest {
    @Autowired
    AuditLogDao auditLogDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("auditLogTestData.xml");
        AuditLogDaoImpl.LIMIT = 2;
    }

    @Test
    public void shouldGiveAllLogsCreatedAfterGivenDateTime() throws Exception {
        Date startDateTime = BahmniDateUtil.convertToDate("2017-03-15T16:57:09.0Z", BahmniDateUtil.DateFormatType.UTC);
        List<AuditLog> logs = auditLogDao.getLogs(null, null, startDateTime, null, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals(Integer.valueOf(3), auditLog_1.getUserId());
        assertEquals(Integer.valueOf(11), auditLog_1.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals(Integer.valueOf(1), auditLog_2.getUserId());
        assertEquals(Integer.valueOf(10), auditLog_2.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void shouldGivePreviousLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, 5, true);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals(Integer.valueOf(3), auditLog_1.getUserId());
        assertEquals(Integer.valueOf(11), auditLog_1.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals(Integer.valueOf(1), auditLog_2.getUserId());
        assertEquals(Integer.valueOf(10), auditLog_2.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void shouldGiveNextLogsFromGivenIndex() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, 2, false);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("EDIT_CLINICAL message", auditLog_1.getMessage());
        assertEquals("EDIT_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(3), auditLog_1.getAuditLogId());
        assertEquals(Integer.valueOf(3), auditLog_1.getUserId());
        assertEquals(Integer.valueOf(11), auditLog_1.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87c", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals(Integer.valueOf(1), auditLog_2.getUserId());
        assertEquals(Integer.valueOf(10), auditLog_2.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }

    @Test
    public void shouldGiveResentLogsInDescendingOrder() throws Exception {
        List<AuditLog> logs = auditLogDao.getLogs(null, null, null, null, null);
        assertEquals(2, logs.size());
        AuditLog auditLog_1 = logs.get(0);
        AuditLog auditLog_2 = logs.get(1);

        assertEquals("VIEWED_CLINICAL_DASHBOARD message", auditLog_1.getMessage());
        assertEquals("VIEWED_CLINICAL", auditLog_1.getEventType());
        assertEquals(Integer.valueOf(5), auditLog_1.getAuditLogId());
        assertEquals(Integer.valueOf(4), auditLog_1.getUserId());
        assertEquals(Integer.valueOf(9), auditLog_1.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87e", auditLog_1.getUuid());

        assertEquals("VIEWED_DASHBOARD message", auditLog_2.getMessage());
        assertEquals("VIEWED_DASHBOARD", auditLog_2.getEventType());
        assertEquals(Integer.valueOf(4), auditLog_2.getAuditLogId());
        assertEquals(Integer.valueOf(1), auditLog_2.getUserId());
        assertEquals(Integer.valueOf(10), auditLog_2.getPatientId());
        assertEquals("86526ed5-3c11-11de-a0ba-001e378eb87d", auditLog_2.getUuid());
    }
}