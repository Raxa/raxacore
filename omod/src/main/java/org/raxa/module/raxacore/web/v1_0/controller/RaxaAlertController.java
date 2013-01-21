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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.RaxaAlert;
import org.raxa.module.raxacore.RaxaAlertService;
import org.raxa.module.raxacore.web.v1_0.resource.RaxaAlertResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the RaxaAlert resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/raxaalert")
public class RaxaAlertController {
	
	RaxaAlertService service;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "name", "description" };
	
	/**
	 * Before each function, initialize our service
	 */
	public void initRaxaAlertController() {
		service = Context.getService(RaxaAlertService.class);
	}
	
	/**
	 * Create new Raxa Alert by POST'ing atleast name and description property in the request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and PatientList object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New RaxaAlert")
	@ResponseBody
	public Object createNewRaxaAlert(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initRaxaAlertController();
		RaxaAlert raxaAlert = setPostFields(new RaxaAlert(), post);
		raxaAlert.setSeen(Boolean.FALSE);
		RaxaAlert created = service.saveRaxaAlert(raxaAlert);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return RestUtil.created(response, obj);
	}
	
	/**
	 * Helper function to set fields from a POST call
	 *
	 * @param rAlert our Raxa Alert to change
	 * @param post our REST call
	 * @return the changed raxa alert
	 */
	private RaxaAlert setPostFields(RaxaAlert raxaAlert, SimpleObject post) {
		if (post.get("providerRecipient") != null) {
			Provider pRecipient = Context.getProviderService().getProviderByUuid(post.get("providerRecipient").toString());
			raxaAlert.setProviderRecipientId(pRecipient.getId());
			raxaAlert.setProviderRecipient(pRecipient);
		}
		if (post.get("providerSent") != null) {
			Provider pSender = Context.getProviderService().getProviderByUuid(post.get("providerSent").toString());
			raxaAlert.setProviderSentId(pSender.getId());
			raxaAlert.setProviderSent(pSender);
		}
		if (post.get("toLocation") != null) {
			Location toLocation = Context.getLocationService().getLocationByUuid(post.get("toLocation").toString());
			raxaAlert.setToLocationId(toLocation.getId());
			raxaAlert.setToLocation(toLocation);
		}
		if (post.get("fromLocation") != null) {
			Location fromLocation = Context.getLocationService().getLocationByUuid(post.get("fromLocation").toString());
			raxaAlert.setFromLocationId(fromLocation.getId());
			raxaAlert.setFromLocation(fromLocation);
		}
		if (post.get("patient") != null) {
			Patient patient = Context.getPatientService().getPatientByUuid(post.get("patient").toString());
			raxaAlert.setPatientId(patient.getId());
			raxaAlert.setPatient(patient);
		}
		if (post.get("name") != null) {
			raxaAlert.setName(post.get("name").toString());
		}
		if (post.get("description") != null) {
			raxaAlert.setDescription(post.get("description").toString());
		}
		if (post.get("alertType") != null) {
			raxaAlert.setAlertType(post.get("alertType").toString());
		}
		if (post.get("defaultTask") != null) {
			raxaAlert.setDefaultTask(post.get("defaultTask").toString());
		}
		if (post.get("seen") != null) {
			raxaAlert.setSeen(Boolean.parseBoolean(post.get("seen").toString()));
		}
		if (post.get("time") != null) {
			String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
			        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
			for (int i = 0; i < supportedFormats.length; i++) {
				try {
					Date date = new SimpleDateFormat(supportedFormats[i]).parse(post.get("time").toString());
					raxaAlert.setTime(date);
				}
				catch (Exception ex) {}
			}
		}
		return raxaAlert;
	}
	
	/**
	 *
	 * @param rAlert
	 * @return SimpleObject the representation of Raxa Alert
	 */
	private SimpleObject getFieldsFromRaxaAlert(RaxaAlert rAlert) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", rAlert.getUuid());
		obj.add("name", rAlert.getName());
		obj.add("description", rAlert.getDescription());
		SimpleObject pRecipientObj = new SimpleObject();
		Provider pRecipient = rAlert.getProviderRecipient();
		if (pRecipient != null) {
			pRecipientObj.add("uuid", pRecipient.getUuid());
			pRecipientObj.add("display", pRecipient.getName());
		}
		obj.add("providerRecipient", pRecipientObj);
		SimpleObject pSentObj = new SimpleObject();
		Provider pSent = rAlert.getProviderSent();
		if (pSent != null) {
			pSentObj.add("uuid", pSent.getUuid());
			pSentObj.add("display", pSent.getName());
		}
		obj.add("providerSent", pSentObj);
		SimpleObject toLocationObj = new SimpleObject();
		Location toLocation = rAlert.getToLocation();
		if (toLocation != null) {
			toLocationObj.add("uuid", toLocation.getUuid());
			toLocationObj.add("display", toLocation.getName());
		}
		obj.add("toLocation", toLocationObj);
		SimpleObject fromLocationObj = new SimpleObject();
		Location fromLocation = rAlert.getFromLocation();
		if (fromLocation != null) {
			fromLocationObj.add("uuid", fromLocation.getUuid());
			fromLocationObj.add("display", fromLocation.getName());
		}
		obj.add("fromLocation", fromLocationObj);
		obj.add("alertType", rAlert.getAlertType());
		obj.add("defaultTask", rAlert.getDefaultTask());
		obj.add("seen", rAlert.getSeen());
		obj.add("time", rAlert.getTime());
		return obj;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Raxa Alerts in the system")
	@ResponseBody()
	public String getAllRaxaAlerts(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initRaxaAlertController();
		List<RaxaAlert> allRaxaAlert = service.getAllRaxaAlerts(true);
		ArrayList results = new ArrayList();
		for (RaxaAlert raxaAlert : allRaxaAlert) {
			results.add(getFieldsFromRaxaAlert(raxaAlert));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Fetch raxa alerts according to provider
	 *
	 * @param providerRecipient
	 * @param request
	 * @param response
	 * @return encounters for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "providerRecipient")
	@WSDoc("Fetch all non-retired alerts according to providerRecipient")
	@ResponseBody()
	public String searchByProviderRecipient(@RequestParam("providerRecipient") String providerRecipient,
	        HttpServletRequest request) throws ResponseException {
		initRaxaAlertController();
		return alertListToJson(service.getRaxaAlertByProviderRecipientUuid(providerRecipient, false));
	}
	
	/**
	 * Fetch raxa alerts according to provider
	 *
	 * @param providerRecipient
	 * @param request
	 * @param response
	 * @return encounters for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "toLocation")
	@WSDoc("Fetch all non-retired alerts according to toLocation")
	@ResponseBody()
	public String searchByToLocation(@RequestParam("toLocation") String toLocation, HttpServletRequest request)
	        throws ResponseException {
		initRaxaAlertController();
		return alertListToJson(service.getRaxaAlertByToLocationUuid(toLocation, false));
	}
	
	/**
	 * Helper function that parses a list of Raxa Alerts, returns a JSon
	 */
	private String alertListToJson(List<RaxaAlert> raxaAlerts) {
		ArrayList results = new ArrayList();
		for (RaxaAlert raxaAlert : raxaAlerts) {
			results.add(getFieldsFromRaxaAlert(raxaAlert));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Updates the Raxa Alert by making a POST call with uuid in URL
	 *
	 * @param uuid the uuid for the raxa alert resource
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing raxa alert")
	@ResponseBody
	public Object updateRaxaAlert(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initRaxaAlertController();
		RaxaAlert rAlert = service.getRaxaAlertByUuid(uuid);
		rAlert = setPostFields(rAlert, post);
		RaxaAlert created = service.updateRaxaAlert(rAlert);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return RestUtil.noContent(response);
	}
	
	/**
	 * Get the Raxa Alert by uuid
	 * @param uuid
	 * @param request
	 * @return response string
	 * @throws ResponseException 
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Raxa Alert for the given uuid")
	@ResponseBody()
	public String getRaxaAlertByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request) throws ResponseException {
		initRaxaAlertController();
		RaxaAlert rAlert = service.getRaxaAlertByUuid(uuid);
		return gson.toJson(getFieldsFromRaxaAlert(rAlert));
	}
	
}
//Currently a bug exists in serializer that won't allow us to use resources automatically
////http://tickets.openmrs.org/browse/TRUNK-2205
//@Controller
//@RequestMapping(value = "/rest/v1/raxacore/raxaalert")
//public class RaxaAlertController extends BaseCrudController<RaxaAlertResource> {
//	
//	/**
//	 * Fetch encounters for a given patient
//	 * @param patientUniqueId
//	 * @param request
//	 * @param response
//	 * @return encounters for the given patient
//	 * @throws ResponseException
//	 */
//	@RequestMapping(method = RequestMethod.GET, params = "provider")
//	@WSDoc("Fetch all non-retired alerts for a provider with the given uuid")
//	@ResponseBody
//	public SimpleObject searchByPatient(@RequestParam("provider") String providerUniqueId, HttpServletRequest request,
//	        HttpServletResponse response) throws ResponseException {
//		RaxaAlertResource raxaAlertResource = getResource();
//		RequestContext context = RestUtil.getRequestContext(request);
//		return raxaAlertResource.getRaxaAlertsByProvider(providerUniqueId, context);
//	}
//};
//
