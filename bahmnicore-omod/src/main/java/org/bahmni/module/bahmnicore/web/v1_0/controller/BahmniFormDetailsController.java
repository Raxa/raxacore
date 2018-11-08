package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;
import org.bahmni.module.bahmnicore.service.BahmniFormDetailsService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;


@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/forms")
public class BahmniFormDetailsController extends BaseRestController {

    private BahmniFormDetailsService bahmniFormDetailsService;

    @Autowired
    public BahmniFormDetailsController(BahmniFormDetailsService bahmniFormDetailsService) {
        this.bahmniFormDetailsService = bahmniFormDetailsService;
    }

    /**
     * To fetch all the forms available for a patient.
     *
     * @param patientUuid mandatory patient uuid
     * @param formType    optional parameter to fetch type of forms. "v1" fetches AllObservationTemplate Forms
     *                    whereas "v2" fetches form builder forms. The default is "v2".
     *                    Refer {@link org.bahmni.module.bahmnicore.contract.form.helper.FormType}
     * @return collection of form Details. Refer {@link FormDetails}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<FormDetails> getFormDetails(
            @RequestParam(value = "patientUuid") String patientUuid,
            @RequestParam(value = "formType", required = false) String formType) {

        return bahmniFormDetailsService.getFormDetails(patientUuid, formType);
    }
}

