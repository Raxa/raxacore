package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/visit")
public class BahmniVisitController extends BaseRestController {

    private VisitService visitService;

    @Autowired
    public BahmniVisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "endVisit")
    @ResponseBody
    public Visit endVisitNow(@RequestParam(value = "visitId") Integer visitId) {
        Visit visit = Context.getVisitService().getVisit(visitId);
        return visitService.endVisit(visit, null);
    }


}
