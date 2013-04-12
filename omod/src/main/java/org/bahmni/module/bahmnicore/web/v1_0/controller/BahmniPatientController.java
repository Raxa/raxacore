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

	PatientService service;
	
	private static final String[] REQUIREDFIELDS = { "names", "gender" };
    private BillingService billingService;
    private PatientMapper patientMapper;
    private final Log log = LogFactory.getLog(BahmniPatientController.class);

    @Autowired
    public BahmniPatientController(BillingService billingService) {
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

	@RequestMapping(method = RequestMethod.POST, value = "/patient")
	@WSDoc("Save New Patient")
	@ResponseBody
	public Object createNewPatient(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		validatePost(post);
		BahmniPatient bahmniPerson = new BahmniPatient(post);

		Patient patient = null;

        patient = updatePatient(bahmniPerson, patient);
        createCustomerForBilling(patient, patient.getPatientIdentifier().toString());

        return RestUtil.created(response, getPatientAsSimpleObject(patient));
	}

    @RequestMapping(method = RequestMethod.POST, value = "/patient/{patientUuid}")
    @WSDoc("Update existing patient")
	@ResponseBody
	public Object updatePatient(@PathVariable("patientUuid") String patientUuid, @RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		validatePost(post);
		BahmniPatient bahmniPerson = new BahmniPatient(post);

		Patient patient = getPatientService().getPatientByUuid(patientUuid);

        Patient savedPatient = updatePatient(bahmniPerson, patient);

        return RestUtil.created(response, getPatientAsSimpleObject(savedPatient));
	}

    private Patient updatePatient(BahmniPatient bahmniPerson, Patient patient) {
        patient = getPatientMapper().map(patient, bahmniPerson);
        Patient savedPatient = getPatientService().savePatient(patient);
        return savedPatient;
    }

    private void createCustomerForBilling(Patient patient, String patientId) {
        String name = patient.getPersonName().getFullName();
        try{
            billingService.createCustomer(name, patientId);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setPatientMapper(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    private PatientMapper getPatientMapper() {
        if(patientMapper == null) patientMapper =   new PatientMapper(new PersonNameMapper(), new BirthDateMapper(), new PersonAttributeMapper(),
		        new AddressMapper(), new PatientIdentifierMapper(), new HealthCenterMapper());
        return patientMapper;
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
