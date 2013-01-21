package org.raxa.module.raxacore.web.v1_0.controller;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Relationship resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/patientaccess")
public class RaxaPatientAccessController extends BaseRestController {
	
	PersonService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REQUIRED_FIELDS = { "fromPerson", "toPerson", "relationshipType" };
	
	private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
	        "EEE MMM d yyyy HH:mm:ss zZzzzz", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
	        "yyyy-MM-dd" };
	
	public void initRelationshipController() {
		service = Context.getPersonService();
	}
	
	private void validatePost(SimpleObject post) throws ResponseException {
		for (int i = 0; i < REQUIRED_FIELDS.length; i++) {
			if (post.get(REQUIRED_FIELDS[i]) == null) {
				throw new ResponseException(
				                            "Required field " + REQUIRED_FIELDS[i] + " not found") {};
			}
		}
	}
	
	/**
	 * Create new relationship between 2 persons by POST
	 * request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and Relationship object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New Relationship")
	@ResponseBody
	public Object createNewRelationship(@RequestBody SimpleObject post, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initRelationshipController();
		validatePost(post);
		Relationship relationship = createRelationshipFromPost(post);
		relationship = service.saveRelationship(relationship);
		return RestUtil.created(response, getRelationshipAsSimpleObject(relationship));
	}
	
	/**
	 * Creates an relationship based on fields in the post object
	 * @param post
	 * @return 
	 */
	private Relationship createRelationshipFromPost(SimpleObject post) throws ResponseException {
		Relationship relationship = new Relationship();
		Person a, b;
		try {
			Method method = service.getClass().getMethod("getPersonByUuidSafeSearch", String.class);
			a = (Person) method.invoke(service, post.get("fromPerson").toString());
			b = (Person) method.invoke(service, post.get("toPerson").toString());
		}
		catch (Exception e) {
			a = Context.getPersonService().getPersonByUuid(post.get("fromPerson").toString());
			b = Context.getPersonService().getPersonByUuid(post.get("toPerson").toString());
		}
		relationship.setPersonA(a);
		relationship.setPersonB(b);
		relationship.setRelationshipType(Context.getPersonService().getRelationshipTypeByName(
		    post.get("relationshipType").toString()));
		return relationship;
	}
	
	/**
	 * Get the relationships
	 *
	 * @param uuid
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "fromPerson")
	@WSDoc("Gets Full representation of Relationships for the given parameters")
	@ResponseBody()
	public String getRelationships(@RequestParam Map<String, String> params, HttpServletRequest request)
	        throws ResponseException {
		initRelationshipController();
		Person fromPerson = Context.getPersonService().getPersonByUuid(params.get("fromPerson"));
		Person toPerson = Context.getPersonService().getPersonByUuid(params.get("toPerson"));
		RelationshipType rType = Context.getPersonService().getRelationshipTypeByName(params.get("relationshipType"));
		List<Relationship> relationships = service.getRelationships(fromPerson, toPerson, rType);
		ArrayList results = new ArrayList();
		for (Relationship relationship : relationships) {
			results.add(getRelationshipAsSimpleObject(relationship));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Get a specific relationship
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Relationship for the uuid path")
	@ResponseBody()
	public String getRelationshipByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initRelationshipController();
		Relationship relationship = service.getRelationshipByUuid(uuid);
		return gson.toJson(getRelationshipAsSimpleObject(relationship));
	}
	
	/**
	 * Get all the unretired relationships (as REF representation) in the system
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Relationships in the system")
	@ResponseBody()
	public String getAllRelationships(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initRelationshipController();
		List<Relationship> allRelationships = service.getAllRelationships();
		ArrayList results = new ArrayList();
		for (Relationship relationship : allRelationships) {
			results.add(getRelationshipAsSimpleObject(relationship));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Returns a SimpleObject containing some fields of Relationship
	 *
	 * @param relationship
	 * @return
	 */
	private SimpleObject getRelationshipAsSimpleObject(Relationship relationship) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", relationship.getUuid());
		if (relationship.getPersonA() != null) {
			SimpleObject fromPersonObj = new SimpleObject();
			fromPersonObj.add("uuid", relationship.getPersonA().getUuid());
			fromPersonObj.add("display", relationship.getPersonA().getPersonName().getFullName());
			obj.add("fromPerson", fromPersonObj);
		}
		if (relationship.getPersonB() != null) {
			SimpleObject toPersonObj = new SimpleObject();
			toPersonObj.add("uuid", relationship.getPersonB().getUuid());
			toPersonObj.add("display", relationship.getPersonB().getPersonName().getFullName());
			obj.add("toPerson", toPersonObj);
		}
		obj.add("relationshipType", relationship.getRelationshipType().getaIsToB() + "/"
		        + relationship.getRelationshipType().getbIsToA());
		return obj;
	}
	
}
