package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.admin.config.model.BahmniConfig;
import org.bahmni.module.admin.config.service.BahmniConfigService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmni/config")
public class BahmniConfigController extends BaseRestController {

    @Autowired
    private BahmniConfigService bahmniConfigService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public BahmniConfig get(@RequestParam("appName") String appName, @RequestParam(value = "configName") String configName) {
        return bahmniConfigService.get(appName, configName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{appName}/{configName:.+}")
    @ResponseBody
    public String getConfig(@PathVariable("appName") String appName, @PathVariable(value = "configName") String configName) {
        return bahmniConfigService.get(appName, configName).getConfig();
    }

    @RequestMapping(method = RequestMethod.GET, value = "all")
    @ResponseBody
    public List<BahmniConfig> getAll(@RequestParam("appName") String appName) {
        return bahmniConfigService.getAllFor(appName);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public BahmniConfig insert(@RequestBody BahmniConfig bahmniConfig) {
        return bahmniConfigService.save(bahmniConfig);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public BahmniConfig update(@RequestBody BahmniConfig bahmniConfig) {
        return bahmniConfigService.update(bahmniConfig);
    }

}
