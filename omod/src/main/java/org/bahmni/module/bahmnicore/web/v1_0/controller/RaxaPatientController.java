package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.mapper.*;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Controller for REST web service access to the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/patient")
public class RaxaPatientController extends BaseRestController {
	
	PatientService service;
	
	private static final String[] REQUIREDFIELDS = { "names", "gender" };
	
	public void initPatientController() {
		service = Context.getPatientService();
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
		initPatientController();
		validatePost(post);
		BahmniPatient bahmniPerson = new BahmniPatient(post);
		Patient patient = new PatientMapper(new PersonNameMapper(), new BirthDateMapper(), new PersonAttributeMapper(),
		        new AddressMapper()).map(null, bahmniPerson);
		addHealthCenter(post, patient);
		
		return RestUtil.created(response, getPatientAsSimpleObject(service.savePatient(patient)));
	}
	
	private void addHealthCenter(SimpleObject post, Person person) {
		LocationService locationService = Context.getLocationService();
		List<Location> allLocations = locationService.getAllLocations();
		String center = ((LinkedHashMap) post.get("centerID")).get("name").toString();
		
		List<LocationAttributeType> allLocationAttributeTypes = locationService.getAllLocationAttributeTypes();
		LocationAttributeType identifierSourceName = findIdentifierSourceName(allLocationAttributeTypes);
		
		for (Location location : allLocations) {
			Collection<LocationAttribute> activeAttributes = location.getActiveAttributes();
			for (LocationAttribute attribute : activeAttributes) {
				addHealthCenter(person, center, identifierSourceName, location, attribute);
			}
		}
	}
	
	private void addHealthCenter(Person person, String center, LocationAttributeType identifierSourceName,
	        Location location, LocationAttribute attribute) {
		if (attribute.getAttributeType().equals(identifierSourceName) && attribute.getValue().toString().equals(center)) {
			PersonAttribute locationAttribute = new PersonAttribute();
			locationAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
			locationAttribute.setValue(location.getId().toString());
			person.getAttributes().add(locationAttribute);
		}
	}
	
	private LocationAttributeType findIdentifierSourceName(List<LocationAttributeType> allLocationAttributeTypes) {
		LocationAttributeType identifierSourceName = null;
		for (LocationAttributeType attributeType : allLocationAttributeTypes) {
			if (attributeType.getName().equals("IdentifierSourceName")) {
				identifierSourceName = attributeType;
				break;
			}
		}
		return identifierSourceName;
	}
	
	private boolean validatePost(SimpleObject post) throws ResponseException {
		for (int i = 0; i < REQUIREDFIELDS.length; i++) {
			if (post.get(REQUIREDFIELDS[i]) == null) {
				throw new ResponseException(
				                            "Required field " + REQUIREDFIELDS[i] + " not found") {};
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
