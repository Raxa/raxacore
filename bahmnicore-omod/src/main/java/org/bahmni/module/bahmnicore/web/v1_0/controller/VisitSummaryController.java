package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.VisitSummaryService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/v1/bahmnicore/visitsummary")
public class VisitSummaryController extends BaseRestController {

    @Autowired
    VisitSummaryService visitSummaryService;

    @Autowired
    ConceptService conceptService;


    public VisitSummaryController(VisitSummaryService visitSummaryService) {
        this.visitSummaryService = visitSummaryService;
    }

    public VisitSummaryController() {
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{visitUUID}")
    @WSDoc("Get a summary of the visit")
    @ResponseBody
    public List<EncounterTransaction> get(@PathVariable("visitUUID")  String visitUUID){
        return visitSummaryService.getVisitSummary(visitUUID);
    }
}
