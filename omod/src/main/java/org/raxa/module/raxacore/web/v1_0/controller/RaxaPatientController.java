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

import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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
		
		addHealthCenter(post, person);
		
		if (post.get("attributes") != null) {
			addAttributes(person, post);
		}
		if (post.get("addresses") != null) {
			addAddresses(person, post);
		}
		return RestUtil.created(response, getPatientAsSimpleObject(savePatient(person, post)));
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
			if (addressObjects.get(i).get("countyDistrict") != null) {
				pa.setCountyDistrict(addressObjects.get(i).get("countyDistrict").toString());
			}
			if (addressObjects.get(i).get("stateProvince") != null) {
				pa.setStateProvince(addressObjects.get(i).get("stateProvince").toString());
			}
			p.addAddress(pa);
		}
		return p;
	}
	
	private Patient savePatient(Person person, SimpleObject post) {
		
		Patient patient = new Patient(person);
		
		IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
		List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);
		String center = ((LinkedHashMap) post.get("centerID")).get("name").toString();
		for (IdentifierSource identifierSource : allIdentifierSources) {
			if (identifierSource.getName().equals(center)) {
				String identifier = identifierSourceService.generateIdentifier(identifierSource, "Generated by me");
				PatientIdentifierType identifierType = identifierSource.getIdentifierType();
				PatientIdentifier patientIdentifier = new PatientIdentifier(identifier, identifierType, null);
				patientIdentifier.setPreferred(true);
				patient.addIdentifier(patientIdentifier);
				break;
			}
		}
		return service.savePatient(patient);
	}
	
	/**
	 * Returns a SimpleObject containing some fields of Patient
	 *
	 * @param p
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
