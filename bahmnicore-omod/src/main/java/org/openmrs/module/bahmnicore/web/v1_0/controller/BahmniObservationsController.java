package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.openmrs.Obs;
import org.openmrs.module.bahmnicore.web.v1_0.mapper.BahmniObservationsMapper;
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
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/observations")
public class BahmniObservationsController extends BaseRestController {
    @Autowired
    private BahmniPersonObsService personObsService;

    @Autowired
    public BahmniObservationsController(BahmniPersonObsService personObsService) {
        this.personObsService = personObsService;
    }

    public BahmniObservationsController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ObservationData> get(@RequestParam(value = "patientUuid", required = true) String patientUUID,
                                     @RequestParam(value = "numberOfVisits", required = false) Integer numberOfVisits,
                                     @RequestParam(value = "conceptName", required = true) String[] conceptNames) {
        List<Obs> obsForPerson = personObsService.getObsForPersonAndConceptNameAndNumberOfVisits(patientUUID, conceptNames, numberOfVisits);
        List<ObservationData> observationDataList = new BahmniObservationsMapper().map(obsForPerson);
        return observationDataList;
    }
}
