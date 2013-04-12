package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientServiceImpl;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for REST web service access to
 * the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/bahmnicore")
public class BahmniPatientController extends BaseRestController {

	private static final String[] REQUIREDFIELDS = { "names", "gender" };
    private BahmniPatientServiceImpl bahmniPatientService;

    @Autowired
    public BahmniPatientController(BillingService billingService) {
        this.bahmniPatientService = new BahmniPatientServiceImpl(billingService);
    }

    public void setPatientService(PatientService patientService) {
		this.bahmniPatientService.setPatientService(patientService);
	}

    @RequestMapping(method = RequestMethod.POST, value = "/patient")
	@WSDoc("Save New Patient")
	@ResponseBody
	public Object createNewPatient(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		validatePost(post);
		BahmniPatient bahmniPatient = new BahmniPatient(post);
        Patient patient = bahmniPatientService.createPatient(bahmniPatient);

        return RestUtil.created(response, getPatientAsSimpleObject(patient));
	}

    @RequestMapping(method = RequestMethod.POST, value = "/patient/{patientUuid}")
    @WSDoc("Update existing patient")
	@ResponseBody
	public Object updatePatient(@PathVariable("patientUuid") String patientUuid, @RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		validatePost(post);
		BahmniPatient bahmniPatient = new BahmniPatient(post);
        bahmniPatient.setUuid(patientUuid);

        Patient patient = bahmniPatientService.updatePatient(bahmniPatient);

        return RestUtil.created(response, getPatientAsSimpleObject(patient));
	}


    public void setPatientMapper(PatientMapper patientMapper) {
        this.bahmniPatientService.setPatientMapper(patientMapper);
    }

	private boolean validatePost(SimpleObject post) throws ResponseException {
		for (int i = 0; i < REQUIREDFIELDS.length; i++) {
			if (post.get(REQUIREDFIELDS[i]) == null) {
				throw new ResponseException("Required field " + REQUIREDFIELDS[i] + " not found") {};
			}
		}
		return true;
	}
	
	private SimpleObject getPatientAsSimpleObject(Patient p) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", p.getUuid());
		obj.add("name", p.getGivenName() + " " + p.getFamilyName());
		obj.add("identifier", p.getPatientIdentifier().getIdentifier());
		return obj;
	}
	
}
