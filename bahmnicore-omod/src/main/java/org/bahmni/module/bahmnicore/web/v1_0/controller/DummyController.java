package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterConfigResponse;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/bahmnidummy")
public class DummyController extends BaseRestController {

    public DummyController() {
    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public String getConfig() {
        return "dummy done";
    }

}
