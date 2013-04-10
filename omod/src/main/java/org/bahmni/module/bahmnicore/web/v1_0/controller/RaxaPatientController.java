package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.mapper.*;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.billing.BillingService;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Controller for REST web service access to
 * the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/patient")
public class RaxaPatientController extends BaseRestController {
	
	PatientService service;
	
	private static final String[] REQUIREDFIELDS = { "names", "gender" };
    private BillingService billingService;
    private final Log log = LogFactory.getLog(RaxaPatientController.class);

    @Autowired
    public RaxaPatientController(BillingService billingService) {
        this.billingService = billingService;
    }

    public void setPatientService(PatientService patientService) {
		this.service = patientService;
	}

    private PatientService getPatientService() {
        if (service == null)
            service = Context.getPatientService();
        return service;
    }

	/**
	 * Create new patient by POST'ing at least name and gender property in the
	 * request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and Drug object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New Patient")
	@ResponseBody
	public Object createNewPatient(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		validatePost(post);
		BahmniPatient bahmniPerson = new BahmniPatient(post);

		Patient patient = null;
		List<Patient> patients = getPatientService().getPatients(bahmniPerson.getPatientIdentifier());
		if (patients.size() > 0)
			patient = patients.get(0);
		patient = getPatientMapper().map(patient, bahmniPerson);
        Patient savedPatient = getPatientService().savePatient(patient);
        String patientId = patient.getPatientIdentifier().toString();
        String name = patient.getPersonName().getFullName();
        billingService.tryCreateCustomer(name, patientId);
        return RestUtil.created(response, getPatientAsSimpleObject(savedPatient));
	}

	private PatientMapper getPatientMapper() {
		return new PatientMapper(new PersonNameMapper(), new BirthDateMapper(), new PersonAttributeMapper(),
		        new AddressMapper(), new PatientIdentifierMapper(), new HealthCenterMapper());
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
