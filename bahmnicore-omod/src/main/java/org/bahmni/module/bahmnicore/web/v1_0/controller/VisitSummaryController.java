package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.VisitSummary;
import org.openmrs.module.emrapi.visit.EmrVisitService;
import org.openmrs.module.emrapi.visit.contract.VisitRequest;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/rest/v1/bahmnicore/visitsummary")
public class VisitSummaryController {

    @Autowired
    EmrVisitService emrVisitService;

    public VisitSummaryController(EmrVisitService emrVisitService) {
        this.emrVisitService = emrVisitService;
    }

    public VisitSummaryController() {
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{visitUUID}")
    @WSDoc("Get a summary of the visit")
    public VisitSummary get(String visitUUID){
        return new VisitSummary(emrVisitService.find(new VisitRequest(visitUUID)));
    }
}
