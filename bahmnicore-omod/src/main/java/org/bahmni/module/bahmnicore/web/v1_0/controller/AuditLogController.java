package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.auditlog.mapper.AuditLogMapper;
import org.bahmni.module.admin.auditlog.service.AuditLogDaoService;
import org.bahmni.module.bahmnicore.util.BahmniDateUtil;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/admin")
public class AuditLogController {
    @Autowired
    AuditLogDaoService auditLogDaoService;

    @RequestMapping(value = "/auditLog", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<AuditLogMapper> getLogs(@RequestParam(value = "username", required = false) String username,
                                             @RequestParam(value = "patientId", required = false) String patientId,
                                             @RequestParam(value = "startFrom", required = false) String startFrom,
                                             @RequestParam(value = "lastAuditLogId", required = false) Integer lastAuditLogId,
                                             @RequestParam(value = "prev", required = false) Boolean prev,
                                             @RequestParam(value = "defaultView", required = false) Boolean defaultView) throws ParseException {
        UserContext userContext = Context.getUserContext();
        if (userContext.isAuthenticated()) {
            if (userContext.hasPrivilege("admin")) {
                Date startDateTime = BahmniDateUtil.convertToLocalDateFromUTC(startFrom);
                if (prev == null) {
                    prev = false;
                }
                if(defaultView == null){
                    defaultView = false;
                }
                return auditLogDaoService.getLogs(username, patientId, startDateTime, lastAuditLogId, prev, defaultView);
            } else {
                throw new APIException("User is logged in but does not have sufficient privileges");
            }
        } else {
            throw new APIAuthenticationException("User is not logged in");
        }
    }
}
