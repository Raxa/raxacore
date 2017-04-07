package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;
import org.bahmni.module.bahmnicore.service.AuditLogService;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/auditlog")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<SimpleObject> getLogs(@RequestParam(value = "username", required = false) String username,
                                           @RequestParam(value = "patientId", required = false) String patientId,
                                           @RequestParam(value = "startFrom", required = false) String startFrom,
                                           @RequestParam(value = "lastAuditLogId", required = false) Integer lastAuditLogId,
                                           @RequestParam(value = "prev", required = false, defaultValue = "false") Boolean prev,
                                           @RequestParam(value = "defaultView", required = false, defaultValue = "false") Boolean defaultView) throws ParseException {
        UserContext userContext = Context.getUserContext();
        if (userContext.isAuthenticated()) {
            if (userContext.hasPrivilege("admin")) {
                Date startDateTime = BahmniDateUtil.convertToLocalDateFromUTC(startFrom);
                return auditLogService.getLogs(username, patientId, startDateTime, lastAuditLogId, prev, defaultView);
            } else {
                throw new APIException("User is logged in but does not have sufficient privileges");
            }
        } else {
            throw new APIAuthenticationException("User is not logged in");
        }

    }

    @RequestMapping( method = RequestMethod.POST)
    @ResponseBody
    public void createAuditLog(@RequestBody AuditLogPayload log) throws IOException {
         auditLogService.createAuditLog(log);
    }
}
