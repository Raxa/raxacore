package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;
import org.bahmni.module.bahmnicore.service.AuditLogService;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({Context.class})
@RunWith(PowerMockRunner.class)
public class AuditLogControllerTest {
    @InjectMocks
    AuditLogController auditLogController;

    @Mock
    UserContext userContext;

    @Mock
    AuditLogService auditLogService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getUserContext()).thenReturn(userContext);
    }

    @Test
    public void shouldExceptionIfUserIsNotLoggedIn() throws Exception {
        when(userContext.isAuthenticated()).thenReturn(false);

        thrown.expect(APIAuthenticationException.class);
        thrown.expectMessage("User is not logged in");
        auditLogController.getLogs("username", "patientId",
                "2017-03-22T18:30:00.000Z", 1, null, null);
        fail();
    }

    @Test
    public void shouldExceptionIfUserIsNOtPrivileges() throws Exception {
        when(userContext.isAuthenticated()).thenReturn(true);
        when(userContext.hasPrivilege("admin")).thenReturn(false);

        thrown.expect(APIException.class);
        thrown.expectMessage("User is logged in but does not have sufficient privileges");
        auditLogController.getLogs("username", "patientId",
                "2017-03-22T18:30:00.000Z", 1, null, null);
        fail();
    }

    @Test
    public void shouldGiveAuditLogs() throws Exception {
        Date startDateTime = BahmniDateUtil.convertToLocalDateFromUTC("2017-03-22T18:30:00.000Z");
        when(userContext.isAuthenticated()).thenReturn(true);
        when(userContext.hasPrivilege("admin")).thenReturn(true);
        when(auditLogService.getLogs("username", "patientId", startDateTime,
                1, null, false)).thenReturn(new ArrayList<>());

        ArrayList<SimpleObject> logs = auditLogController.getLogs("username", "patientId", "2017-03-22T18:30:00.000Z", 1, false, false);
        assertEquals(0, logs.size());
        verify(auditLogService, times(1))
                .getLogs("username", "patientId", startDateTime, 1, false, false);

    }

    @Test
    public void shouldSaveAuditLog() throws Exception{
        AuditLogPayload log = new AuditLogPayload("patientUuid", "message" ,"eventType");
        auditLogController.createAuditLog(log);
        verify(auditLogService, times(1)).createAuditLog(log);
    }
}