package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.model.ResultList;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/conf")
public class BahmniConfigurationController extends BaseRestController {

    private BahmniCoreApiProperties bahmniCoreApiProperties;

    @Autowired
    public BahmniConfigurationController(BahmniCoreApiProperties bahmniCoreApiProperties) {
        this.bahmniCoreApiProperties = bahmniCoreApiProperties;
    }

    @RequestMapping(method = RequestMethod.GET)
	@WSDoc("Returns bahmni configuration")
    @ResponseBody
	public SimpleObject index() {
        return new SimpleObject().add("patientImagesUrl", bahmniCoreApiProperties.getPatientImagesUrl());
    }
}