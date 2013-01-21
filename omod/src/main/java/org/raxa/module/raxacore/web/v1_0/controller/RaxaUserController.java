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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/user")
public class RaxaUserController extends BaseRestController {
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	UserService service;
	
	private static final String[] TYPE = { "patient", "provider" };
	
	private static final String[] REQUIREDFIELDS = { "type", "firstName", "lastName", "gender", "userName", "password",
	        "location" };
	
	public void initUserController() {
		service = Context.getUserService();
	}
	
	private boolean validatePost(SimpleObject post) throws ResponseException {
		for (int i = 0; i < REQUIREDFIELDS.length; i++) {
			if (post.get(REQUIREDFIELDS[i]) == null) {
				throw new ResponseException(
				                            "Required field " + REQUIREDFIELDS[i] + " not found") {};
			}
		}
		if (service.getUserByUsername(post.get("userName").toString()) != null) {
			throw new ResponseException(
			                            "User name must be unique") {};
		}
		OpenmrsUtil.validatePassword(post.get("userName").toString(), post.get("password").toString(), null);
		for (int j = 0; j < TYPE.length; j++) {
			if (post.get("type").equals(TYPE[j])) {
				return true;
			}
		}
		throw new ResponseException(
		                            "User type is unsupported") {};
	}
	
	private Object getUserAsSimpleObject(User u) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", u.getUuid());
		obj.add("display", u.getDisplayString());
		return obj;
	}
	
	/**
	 * Get the user information according to the current user
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Post user information")
	@ResponseBody()
	public Object createNewUser(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initUserController();
		validatePost(post);
		Person person = new Person();
		PersonName name = new PersonName(post.get("firstName").toString(), null, post.get("lastName").toString());
		name.setPreferred(true);
		person.addName(name);
		person.setGender(post.get("gender").toString());
		Location location = Context.getLocationService().getLocationByUuid(post.get("location").toString());
		if (location == null) {
			throw new ResponseException(
			                            "Location uuid not found") {};
		}
		PersonAttribute locationAttribute = new PersonAttribute();
		locationAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		locationAttribute.setValue(location.getId().toString());
		person.addAttribute(locationAttribute);
		if (post.get("email") != null) {
			PersonAttribute emailAttribute = new PersonAttribute();
			emailAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Email"));
			emailAttribute.setValue(post.get("email").toString());
			person.addAttribute(emailAttribute);
		}
		if (post.get("phone") != null) {
			PersonAttribute phoneAttribute = new PersonAttribute();
			phoneAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Primary Contact"));
			phoneAttribute.setValue(post.get("phone").toString());
			person.addAttribute(phoneAttribute);
		}
		if (post.get("donateOrgans") != null) {
			PersonAttribute donateOrgansAttribute = new PersonAttribute();
			donateOrgansAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Donate Organs"));
			donateOrgansAttribute.setValue(post.get("donateOrgans").toString());
			person.addAttribute(donateOrgansAttribute);
		}
		String type = post.get("type").toString();
		if (type.equals(TYPE[0])) {
			person = savePatient(person, post, location);
		} else if (type.equals(TYPE[1])) {
			saveProvider(person, post);
		}
		User user = new User(person);
		user.setUsername(post.get("userName").toString());
		if (type.equals(TYPE[1])) {
			user.addRole(Context.getUserService().getRole("System Developer"));
			user.addRole(Context.getUserService().getRole("Provider"));
		}
		User newUser = service.saveUser(user, post.get("password").toString());
		service.setUserProperty(newUser, OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION, location.getId().toString());
		return RestUtil.created(response, getUserAsSimpleObject(newUser));
		//		return RestUtil.created(response, getDrugAsSimpleObject(drugJustCreated));
	}
	
	private Person savePatient(Person person, SimpleObject post, Location location) {
		//            String locationPrefix = l.getAttributes();
		boolean identifierInUse = true;
		String identifier = "";
		while (identifierInUse) {
			//TODO: set this identifier prefix based on location
			identifier = "NEW" + (int) (Math.random() * 100000);
			if (Context.getPatientService().getPatients(identifier).isEmpty()) {
				identifierInUse = false;
			}
		}
		PatientIdentifier pi = new PatientIdentifier(identifier, Context.getPatientService().getPatientIdentifierTypeByName(
		    "RaxaEMR Identifier Number"), location);
		pi.setPreferred(true);
		Patient patient = new Patient(person);
		patient.addIdentifier(pi);
		System.out.println(patient.getPatientIdentifier());
		int personId = Context.getPatientService().savePatient(patient).getPersonId();
		return (Context.getPersonService().getPerson(personId));
	}
	
	private void saveProvider(Person person, SimpleObject post) {
		boolean identifierInUse = true;
		String identifier = "";
		while (identifierInUse) {
			identifier = "" + (int) (Math.random() * 100000);
			if (Context.getProviderService().getProviderByIdentifier(identifier) == null) {
				identifierInUse = false;
			}
		}
		Provider provider = new Provider();
		provider.setPerson(person);
		provider.setIdentifier(identifier);
		if (post.get("isOutpatientDoctor") == null && post.get("isOutpatientDoctor").equals("true")) {
			//Provider service does not allow us to get the provider attribute by name currently, so have to get all....
			Iterator<ProviderAttributeType> iter = Context.getProviderService().getAllProviderAttributeTypes().iterator();
			while (iter.hasNext()) {
				ProviderAttributeType pAttribType = iter.next();
				if (pAttribType.getName().equals("isOutpatientDoctor")) {
					ProviderAttribute pAttrib = new ProviderAttribute();
					pAttrib.setValue(true);
					pAttrib.setAttributeType(pAttribType);
					provider.addAttribute(pAttrib);
					break;
				}
			}
		}
		Context.getPersonService().savePerson(person);
		Context.getProviderService().saveProvider(provider);
		
	}
}
