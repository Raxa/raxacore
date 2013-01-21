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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Drug resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/login")
public class RaxaLoginController extends BaseRestController {
	
	ConceptService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	/**
	 * Get the login information according to the current user
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get Login information")
	@ResponseBody()
	public String getLoginInfo(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		SimpleObject obj = new SimpleObject();
		if (Context.isAuthenticated()) {
			User u = Context.getAuthenticatedUser();
			Person p = Context.getPersonService().getPersonByUuid(u.getPerson().getUuid());
			Provider provider = Context.getProviderService().getProvidersByPerson(p).iterator().next();
			obj.add("personUuid", p.getUuid());
			if (provider != null) {
				obj.add("providerUuid", provider.getUuid());
				ArrayList attributesObj = new ArrayList();
				Iterator<ProviderAttribute> attributesIterator = provider.getActiveAttributes().iterator();
				while (attributesIterator.hasNext()) {
					SimpleObject attributeObj = new SimpleObject();
					ProviderAttribute pAttribute = attributesIterator.next();
					attributeObj.add("attributeType", pAttribute.getAttributeType().getName());
					attributeObj.add("value", pAttribute.getValue());
					attributesObj.add(attributeObj);
				}
				obj.add("providerAttributes", attributesObj);
			}
			obj.add("display", p.getPersonName().getFullName());
			if (p.getAttribute("Health Center") != null) {
				obj.add("location", Context.getLocationService().getLocation(
				    Integer.parseInt(p.getAttribute("Health Center").getValue())).getUuid());
			}
			obj.add("roles", u.getAllRoles());
			obj.add("privileges", u.getPrivileges());
			obj.add("serverTime", Calendar.getInstance().getTime());
			return gson.toJson(obj);
		} else {
			throw new ResponseException(
			                            "Not Authenticated") {};
		}
	}
	
}
