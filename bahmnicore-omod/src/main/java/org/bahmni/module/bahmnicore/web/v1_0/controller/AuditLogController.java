package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.admin.auditLog.model.AuditLog;
import org.bahmni.module.admin.auditLog.service.AuditLogDaoService;
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
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/admin")
public class AuditLogController {
    @Autowired
    AuditLogDaoService auditLogDaoService;

    @RequestMapping(value = "/auditLog", method = RequestMethod.GET)
    @ResponseBody
    public List<AuditLog> getLogs(@RequestParam(value = "username", required = false) String username,
                                  @RequestParam(value = "patientId", required = false) String patientId,
                                  @RequestParam(value = "startFrom", required = false) String startFrom,
                                  @RequestParam(value = "lastAuditLogId", required = false) Integer lastAuditLogId,
                                  @RequestParam(value = "prev", required = false) Boolean prev) throws ParseException {
        UserContext userContext = Context.getUserContext();
        if (userContext.isAuthenticated()) {
            if (userContext.hasPrivilege("admin")) {
                Date startDateTime = BahmniDateUtil.convertToDate(startFrom, BahmniDateUtil.DateFormatType.UTC);
                if (prev == null) {
                    prev = false;
                }
                return auditLogDaoService.getLogs(username, patientId, startDateTime, lastAuditLogId, prev);
            } else {
                throw new APIException("User is logged in but does not have sufficient privileges");
            }
        } else {
            throw new APIAuthenticationException("User is not logged in");
        }
    }
}
