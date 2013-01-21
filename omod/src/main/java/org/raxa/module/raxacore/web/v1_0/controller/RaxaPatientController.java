package org.raxa.module.raxacore.web.v1_0.controller;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/patient")
public class RaxaPatientController extends BaseRestController {
	
	PatientService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REQUIREDFIELDS = { "names", "gender" };
	
	private static final String[] REF = { "uuid", "name", "description" };
	
	public void initPatientController() {
		service = Context.getPatientService();
	}
	
	private boolean validatePost(SimpleObject post) throws ResponseException {
		for (int i = 0; i < REQUIREDFIELDS.length; i++) {
			if (post.get(REQUIREDFIELDS[i]) == null) {
				throw new ResponseException(
				                            "Required field " + REQUIREDFIELDS[i] + " not found") {};
			}
		}
		User u = Context.getAuthenticatedUser();
		Person p = Context.getPersonService().getPersonByUuid(u.getPerson().getUuid());
		if (p.getAttribute("Health Center") == null) {
			throw new ResponseException(
			                            "Current user needs Health Center attribute") {};
		}
		return true;
	}
	
	/**
	 * Returns the Resource Version
	 */
	private String getResourceVersion() {
		return "1.0";
	}
	
	/**
	 * Adds attributes to the given person from the values in the post object
	 */
	private Person addAttributes(Person p, SimpleObject post) throws ResponseException {
		List<LinkedHashMap> attributeObjects = (List<LinkedHashMap>) post.get("attributes");
		for (int i = 0; i < attributeObjects.size(); i++) {
			if (attributeObjects.get(i).get("attributeType") != null && attributeObjects.get(i).get("value") != null) {
				PersonAttribute pa = new PersonAttribute();
				PersonAttributeType paType = Context.getPersonService().getPersonAttributeTypeByUuid(
				    attributeObjects.get(i).get("attributeType").toString());
				if (paType == null) {
					throw new ResponseException(
					                            "Person Attribute Type not found") {};
				}
				pa.setAttributeType(paType);
				String paValue = attributeObjects.get(i).get("value").toString();
				if (paValue == null) {
					throw new ResponseException(
					                            "Person Attribute Value cannot be null") {};
				}
				pa.setValue(paValue);
				p.addAttribute(pa);
			}
		}
		return p;
	}
	
	/**
	 * Adds names to the given person from the values in the post object
	 */
	private Person addNames(Person p, SimpleObject post) throws ResponseException {
		List<LinkedHashMap> nameObjects = (List<LinkedHashMap>) post.get("names");
		for (int i = 0; i < nameObjects.size(); i++) {
			String first = "", middle = "", last = "";
			if (nameObjects.get(i).get("givenName") != null) {
				first = nameObjects.get(i).get("givenName").toString();
			}
			if (nameObjects.get(i).get("middleName") != null) {
				middle = nameObjects.get(i).get("middleName").toString();
			}
			if (nameObjects.get(i).get("familyName") != null) {
				last = nameObjects.get(i).get("familyName").toString();
			}
			PersonName name = new PersonName(first, middle, last);
			if (i == 0) {
				name.setPreferred(Boolean.TRUE);
			}
			p.addName(name);
		}
		return p;
	}
	
	/**
	 * Adds the address to the given person from the post object
	 */
	private Person addAddresses(Person p, SimpleObject post) throws ResponseException {
		List<LinkedHashMap> addressObjects = (List<LinkedHashMap>) post.get("addresses");
		for (int i = 0; i < addressObjects.size(); i++) {
			PersonAddress pa = new PersonAddress();
			if (i == 0) {
				pa.setPreferred(Boolean.TRUE);
			}
			if (addressObjects.get(i).get("address1") != null) {
				pa.setAddress1(addressObjects.get(i).get("address1").toString());
			}
			if (addressObjects.get(i).get("address2") != null) {
				pa.setAddress2(addressObjects.get(i).get("address2").toString());
			}
			if (addressObjects.get(i).get("address3") != null) {
				pa.setAddress3(addressObjects.get(i).get("address3").toString());
			}
			if (addressObjects.get(i).get("cityVillage") != null) {
				pa.setCityVillage(addressObjects.get(i).get("cityVillage").toString());
			}
			if (addressObjects.get(i).get("stateProvince") != null) {
				pa.setStateProvince(addressObjects.get(i).get("stateProvince").toString());
			}
			p.addAddress(pa);
		}
		return p;
	}
	
	private Patient savePatient(Person person, SimpleObject post, Location location) {
		boolean identifierInUse = true;
		String identifier = "";
		Iterator<LocationAttribute> iter = location.getAttributes().iterator();
		String prefix = "NEW";
		while (iter.hasNext()) {
			LocationAttribute la = iter.next();
			if (la.getAttributeType().getName().equals("identifierPrefix")) {
				prefix = la.getValue().toString();
			}
		}
		while (identifierInUse) {
			//TODO: set this identifier prefix based on location
			identifier = prefix + (int) (Math.random() * 100000);
			if (service.getPatients(identifier).isEmpty()) {
				identifierInUse = false;
			}
		}
		PatientIdentifier pi = new PatientIdentifier(identifier, service
		        .getPatientIdentifierTypeByName("RaxaEMR Identifier Number"), location);
		pi.setPreferred(true);
		Patient patient = new Patient(person);
		patient.addIdentifier(pi);
		return service.savePatient(patient);
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
		Person person = new Person();
		addNames(person, post);
		person.setGender(post.get("gender").toString());
		if (post.get("birthdate") != null) {
			if (post.get("time") != null) {
				String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
				        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
				for (int i = 0; i < supportedFormats.length; i++) {
					try {
						Date date = new SimpleDateFormat(supportedFormats[i]).parse(post.get("time").toString());
						person.setBirthdate(date);
						person.setBirthdateEstimated(Boolean.FALSE);
					}
					catch (Exception ex) {}
				}
			}
		} else if (post.get("age") != null) {
			person.setBirthdateFromAge(Integer.parseInt(post.get("age").toString()), new Date());
			person.setBirthdateEstimated(Boolean.TRUE);
		}
		//Location location = Context.getLocationService().getLocationByUuid(post.get("location").toString());
		Integer userLocation = Integer.parseInt(Context.getPersonService().getPersonByUuid(
		    Context.getAuthenticatedUser().getPerson().getUuid()).getAttribute("Health Center").getValue());
		Location location = Context.getLocationService().getLocation(userLocation);
		PersonAttribute locationAttribute = new PersonAttribute();
		locationAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		locationAttribute.setValue(location.getId().toString());
		person.addAttribute(locationAttribute);
		if (post.get("attributes") != null) {
			addAttributes(person, post);
		}
		if (post.get("addresses") != null) {
			addAddresses(person, post);
		}
		return RestUtil.created(response, getPatientAsSimpleObject(savePatient(person, post, location)));
	}
	
	/**
	 * Safe search a patient, bypass security but only show non-sensitive fields
	 *
	 * @param safesearch
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "safesearch")
	@WSDoc("Gets Summarized representation of Patients for the given search")
	@ResponseBody()
	public String safeSearchPatients(@RequestParam String safesearch, HttpServletRequest request) throws ResponseException {
		initPatientController();
		List<Patient> patients;
		try {
			Method method = service.getClass().getMethod("getPatientsSafeSearch", String.class);
			patients = (List<Patient>) method.invoke(service, safesearch);
		}
		catch (Exception e) {
			//if openmrs core doesn't have "getPatientsSafeSearch" then use the normal one with security
			patients = service.getPatients(safesearch);
			return gson.toJson(new SimpleObject().add("nosafesearch", null));
		}
		ArrayList results = new ArrayList();
		for (Patient patient : patients) {
			results.add(getPatientAsSimpleObject(patient));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Returns a SimpleObject containing some fields of Patient
	 *
	 * @param patient
	 * @return
	 */
	private SimpleObject getPatientAsSimpleObject(Patient p) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", p.getUuid());
		obj.add("name", p.getGivenName() + " " + p.getFamilyName());
		obj.add("identifier", p.getPatientIdentifier().getIdentifier());
		return obj;
	}
	
}
