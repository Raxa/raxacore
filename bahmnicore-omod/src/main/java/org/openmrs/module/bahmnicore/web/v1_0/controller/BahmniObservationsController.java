package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/observations")
public class BahmniObservationsController extends BaseRestController {
    
    @Autowired
    private BahmniObsService personObsService;

    @Autowired
    private ConceptService conceptService;

    public BahmniObservationsController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<BahmniObservation> get(@RequestParam(value = "patientUuid", required = true) String patientUUID,
                                       @RequestParam(value = "concept", required = true) List<String> rootConceptNames,
                                       @RequestParam(value = "scope", required = false) String scope,
                                       @RequestParam(value = "numberOfVisits", required = false) Integer numberOfVisits) {

        List<Concept> rootConcepts = new ArrayList<>();
        for (String rootConceptName : rootConceptNames) {
            rootConcepts.add(conceptService.getConceptByName(rootConceptName));
        }
        
        List<Obs> observations;
        if ("latest".equals(scope)) {
            observations = personObsService.getLatest(patientUUID, rootConceptNames);
        } else {
            observations = personObsService.observationsFor(patientUUID, rootConcepts, numberOfVisits);
        }
        
        return BahmniObservationMapper.map(observations, rootConcepts);
    }
}
