package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.visit.VisitSummary;
import org.bahmni.module.bahmnicore.mapper.BahmniVisitSummaryMapper;
import org.bahmni.module.bahmnicore.service.BahmniVisitService;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/visit")
public class BahmniVisitController extends BaseRestController {

    private static final String VISIT_STATUS_ATTRIBUTE_TYPE = "Visit Status";
    private static final String IPD_VISIT_STATUS = "IPD";
    private VisitService visitService;
    private BahmniVisitService bahmniVisitService;
    private BahmniVisitSummaryMapper bahmniVisitSummaryMapper;

    @Autowired
    public BahmniVisitController(VisitService visitService, BahmniVisitService bahmniVisitService) {
        this.visitService = visitService;
        this.bahmniVisitService = bahmniVisitService;
        this.bahmniVisitSummaryMapper = new BahmniVisitSummaryMapper();
    }

    @RequestMapping(method = RequestMethod.POST, value = "endVisit")
    @ResponseBody
    public Visit endVisitNow(@RequestParam(value = "visitId") Integer visitId) {
        Visit visit = Context.getVisitService().getVisit(visitId);
        return visitService.endVisit(visit, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "summary")
    @ResponseBody
    public VisitSummary getVisitInfo(@RequestParam(value = "visitUuid") String visitUuid) {
        Visit visit = bahmniVisitService.getVisitSummary(visitUuid);
        if (visit != null) {
            boolean hasAdmissionEncounter = false;
            for (VisitAttribute visitAttribute : visit.getAttributes()) {
                if (VISIT_STATUS_ATTRIBUTE_TYPE.equalsIgnoreCase(visitAttribute.getAttributeType().getName()) && IPD_VISIT_STATUS.equalsIgnoreCase(visitAttribute.getValueReference())) {
                    hasAdmissionEncounter = true;
                }
            }
            return bahmniVisitSummaryMapper.map(visit, hasAdmissionEncounter);
        }
        return null;
    }
}
