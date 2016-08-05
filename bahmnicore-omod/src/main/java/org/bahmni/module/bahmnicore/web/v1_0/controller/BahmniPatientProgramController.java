package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1)
public class BahmniPatientProgramController {

    private BahmniProgramWorkflowService bahmniProgramWorkflowService;

    @Autowired
    public BahmniPatientProgramController(BahmniProgramWorkflowService bahmniProgramWorkflowService) {
        this.bahmniProgramWorkflowService = bahmniProgramWorkflowService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bahmnicore/patientProgram")
    @ResponseBody
    public Boolean isPatientProgramPresentForAttributeNameAndValue(@RequestParam(value = "conceptName", required = true) String conceptName,
                                          @RequestParam(value = "conceptValue", required = true) String conceptValue) {
        List<BahmniPatientProgram> bahmniPatientPrograms = bahmniProgramWorkflowService.getPatientProgramByAttributeNameAndValue(conceptName, conceptValue);
        if(CollectionUtils.isNotEmpty(bahmniPatientPrograms)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
