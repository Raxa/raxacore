package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.module.admin.config.service.BahmniConfigService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmni/config")
public class BahmniConfigController extends BaseRestController {

    @Autowired
    private BahmniConfigService bahmniConfigService;

    @RequestMapping(method = RequestMethod.GET, value = "get")
    @ResponseBody
    public BahmniConfig get(@RequestParam("appName") String appName, @RequestParam("configName") String configName) {
        BahmniConfig bahmniConfig = bahmniConfigService.get(appName, configName);
        return bahmniConfig;
    }
}
