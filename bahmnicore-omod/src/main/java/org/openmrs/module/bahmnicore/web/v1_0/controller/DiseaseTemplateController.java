package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.observation.DiseaseTemplate;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/diseaseTemplates")
public class DiseaseTemplateController extends BaseRestController {

    @Autowired
    private DiseaseTemplateService diseaseTemplateService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<DiseaseTemplate> get(@RequestParam(value = "patientUuid", required = true) String patientUUID) {
        return diseaseTemplateService.allDiseaseTemplatesFor(patientUUID);
    }
}
