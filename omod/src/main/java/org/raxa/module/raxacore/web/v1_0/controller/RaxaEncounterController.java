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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Encounter resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/encounter")
public class RaxaEncounterController extends BaseRestController {
	
	EncounterService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REQUIRED_FIELDS = { "encounterDatetime", "patient", "encounterType" };
	
	private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
	        "EEE MMM d yyyy HH:mm:ss zZzzzz", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
	        "yyyy-MM-dd" };
	
	public void initEncounterController() {
		service = Context.getEncounterService();
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
	 * Create new encounter by POST'ing at least name and description property in the
	 * request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and Encounter object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New Encounter")
	@ResponseBody
	public Object createNewEncounter(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initEncounterController();
		validatePost(post);
		Encounter encounter = createEncounterFromPost(post);
		return RestUtil.created(response, getEncounterAsSimpleObject(encounter));
	}
	
	/**
	 * Creates an encounter based on fields in the post object
	 * @param post
	 * @return 
	 */
	private Encounter createEncounterFromPost(SimpleObject post) throws ResponseException {
		Encounter encounter = new Encounter();
		encounter.setPatient(Context.getPatientService().getPatientByUuid(post.get("patient").toString()));
		for (int i = 0; i < DATE_FORMATS.length; i++) {
			try {
				System.out.println(i);
				Date date = new SimpleDateFormat(DATE_FORMATS[i]).parse(post.get("encounterDatetime").toString());
				encounter.setEncounterDatetime(date);
			}
			catch (Exception ex) {}
		}
		encounter.setEncounterType(service.getEncounterTypeByUuid(post.get("encounterType").toString()));
		if (post.get("location") != null) {
			encounter.setLocation(Context.getLocationService().getLocationByUuid(post.get("location").toString()));
		}
		if (post.get("provider") != null) {
			encounter.setProvider(Context.getPersonService().getPersonByUuid(post.get("provider").toString()));
		}
		Encounter newEncounter = service.saveEncounter(encounter);
		if (post.get("obs") != null) {
			createObsFromPost(post, newEncounter);
		}
		if (post.get("orders") != null) {
			createOrdersFromPost(post, newEncounter);
		}
		return encounter;
	}
	
	/**
	 * Creates and saves the obs from the given post
	 * @param post
	 * @param encounter 
	 */
	private void createObsFromPost(SimpleObject post, Encounter encounter) throws ResponseException {
		List<LinkedHashMap> obsObjects = (List<LinkedHashMap>) post.get("obs");
		for (int i = 0; i < obsObjects.size(); i++) {
			Obs obs = new Obs();
			obs.setPerson(encounter.getPatient());
			obs.setConcept(Context.getConceptService().getConceptByUuid(obsObjects.get(i).get("concept").toString()));
			obs.setObsDatetime(encounter.getEncounterDatetime());
			if (encounter.getLocation() != null) {
				obs.setLocation(encounter.getLocation());
			}
			obs.setEncounter(encounter);
			obs = setObsValue(obs, obsObjects.get(i).get("value"));
			if (obsObjects.get(i).get("comment") != null) {
				obs.setComment(obsObjects.get(i).get("comment").toString());
			}
			Context.getObsService().saveObs(obs, "saving new obs");
		}
	}
	
	private Obs setObsValue(Obs obs, Object value) throws ResponseException {
		if (value != null) {
			if (obs.getConcept().getDatatype().isCoded()) {
				// setValueAsString is not implemented for coded obs (in core)
				Concept valueCoded = (Concept) ConversionUtil.convert(value, Concept.class);
				obs.setValueCoded(valueCoded);
			} else if (obs.getConcept().getDatatype().isComplex()) {
				obs.setValueComplex(value.toString());
			} else {
				if (obs.getConcept().isNumeric()) {
					//get the actual persistent object rather than the hibernate proxy
					ConceptNumeric concept = Context.getConceptService().getConceptNumeric(obs.getConcept().getId());
					String units = concept.getUnits();
					if (StringUtils.isNotBlank(units)) {
						String originalValue = value.toString().trim();
						if (originalValue.endsWith(units))
							value = originalValue.substring(0, originalValue.indexOf(units)).trim();
						else {
							//check that that this value has no invalid units
							try {
								Double.parseDouble(originalValue);
							}
							catch (NumberFormatException e) {
								throw new ResponseException(
								                            originalValue + " has invalid units", e) {};
							}
						}
					}
				}
				try {
					obs.setValueAsString(value.toString());
				}
				catch (Exception e) {
					throw new ResponseException(
					                            "Unable to convert obs value") {};
				}
			}
		} else
			throw new ResponseException(
			                            "The value for an observation cannot be null") {};
		return obs;
	}
	
	/**
	 * Creates and saves the orders from the given post
	 * @param post
	 * @param encounter 
	 */
	private void createOrdersFromPost(SimpleObject post, Encounter encounter) throws ResponseException {
		List<LinkedHashMap> orderObjects = (List<LinkedHashMap>) post.get("orders");
		for (int i = 0; i < orderObjects.size(); i++) {
			Order order = new Order();
			order.setPatient(encounter.getPatient());
			order.setConcept(Context.getConceptService().getConceptByUuid(orderObjects.get(i).get("concept").toString()));
			order
			        .setOrderType(Context.getOrderService().getOrderTypeByUuid(
			            orderObjects.get(i).get("orderType").toString()));
			if (orderObjects.get(i).get("instructions") != null) {
				order.setInstructions(orderObjects.get(i).get("instructions").toString());
			}
			if (orderObjects.get(i).get("startDate") != null) {
				for (int j = 0; j < DATE_FORMATS.length; j++) {
					try {
						Date date = new SimpleDateFormat(DATE_FORMATS[j]).parse(orderObjects.get(i).get("startDate")
						        .toString());
						order.setStartDate(date);
					}
					catch (Exception ex) {
						throw new ResponseException(
						                            "Unable to parse date for Obs") {};
					}
				}
			}
			if (orderObjects.get(i).get("autoExpireDate") != null) {
				for (int j = 0; j < DATE_FORMATS.length; j++) {
					try {
						Date date = new SimpleDateFormat(DATE_FORMATS[j]).parse(orderObjects.get(i).get("autoExpireDate")
						        .toString());
						order.setAutoExpireDate(date);
					}
					catch (Exception ex) {
						throw new ResponseException(
						                            "Unable to parse date for Obs") {};
					}
				}
			}
			order.setEncounter(encounter);
			if (encounter.getProvider() != null) {
				order.setOrderer(Context.getAuthenticatedUser());
			}
			Context.getOrderService().saveOrder(order);
		}
	}
	
	/**
	 * Get the encounter as FULL representation
	 *
	 * @param uuid
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Full representation of Encounter for the uuid path")
	@ResponseBody()
	public String getEncounterByUuidFull(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initEncounterController();
		Encounter encounter = service.getEncounterByUuid(uuid);
		SimpleObject obj = getEncounterAsSimpleObject(encounter);
		return gson.toJson(obj);
	}
	
	/**
	 * Returns a SimpleObject containing some fields of Drug
	 *
	 * @param drug
	 * @return
	 */
	private SimpleObject getEncounterAsSimpleObject(Encounter encounter) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", encounter.getUuid());
		obj.add("display", encounter.toString());
		obj.add("encounterDatetime", encounter.getEncounterDatetime());
		if (encounter.getPatient() != null) {
			SimpleObject patientObj = new SimpleObject();
			patientObj.add("uuid", encounter.getPatient().getUuid());
			patientObj.add("display", encounter.getPatient().getPersonName().getFullName());
			obj.add("patient", patientObj);
		}
		if (encounter.getLocation() != null) {
			SimpleObject locationObj = new SimpleObject();
			locationObj.add("uuid", encounter.getLocation().getUuid());
			locationObj.add("display", encounter.getLocation().getDisplayString());
			obj.add("location", locationObj);
		}
		SimpleObject encounterTypeObj = new SimpleObject();
		encounterTypeObj.add("uuid", encounter.getEncounterType().getUuid());
		encounterTypeObj.add("display", encounter.getEncounterType().getName());
		obj.add("encounterType", encounterTypeObj);
		Set<Obs> obs = encounter.getObs();
		if (!encounter.getObs().isEmpty()) {
			ArrayList obsObjects = new ArrayList();
			Iterator<Obs> obsIter = obs.iterator();
			while (obsIter.hasNext()) {
				Obs currentObs = obsIter.next();
				obsObjects.add(createObjectFromObs(currentObs));
			}
			obj.add("obs", obsObjects);
		}
		Set<Order> orders = encounter.getOrders();
		if (!orders.isEmpty()) {
			ArrayList orderObjects = new ArrayList();
			Iterator<Order> orderIter = orders.iterator();
			while (orderIter.hasNext()) {
				Order currentOrder = orderIter.next();
				orderObjects.add(createObjectFromOrder(currentOrder));
			}
			obj.add("orders", orderObjects);
		}
		return obj;
	}
	
	/**
	 * Helper function to add an order to simpleobject for returning over REST
	 * @param obj
	 * @param order
	 * @return 
	 */
	private SimpleObject createObjectFromOrder(Order order) {
		SimpleObject newOrderObject = new SimpleObject();
		newOrderObject.add("uuid", order.getUuid());
		if (order.getOrderType() != null) {
			SimpleObject orderType = new SimpleObject();
			orderType.add("uuid", order.getOrderType().getUuid());
			orderType.add("display", order.getOrderType().getName());
			newOrderObject.add("orderType", orderType);
		}
		SimpleObject orderConcept = new SimpleObject();
		orderConcept.add("uuid", order.getConcept().getUuid());
		orderConcept.add("display", order.getConcept().getName().getName());
		newOrderObject.add("concept", orderConcept);
		if (order.isDrugOrder()) {
			DrugOrder currentDrugOrder = (DrugOrder) order;
			newOrderObject.add("instructions", currentDrugOrder.getInstructions());
			newOrderObject.add("startDate", currentDrugOrder.getStartDate().toString());
			newOrderObject.add("autoExpireDate", currentDrugOrder.getAutoExpireDate().toString());
			newOrderObject.add("dose", currentDrugOrder.getDose());
			newOrderObject.add("units", currentDrugOrder.getUnits());
			newOrderObject.add("frequency", currentDrugOrder.getFrequency());
			newOrderObject.add("quantity", currentDrugOrder.getQuantity());
			SimpleObject drugObj = new SimpleObject();
			drugObj.add("uuid", currentDrugOrder.getDrug().getUuid());
			drugObj.add("display", currentDrugOrder.getDrug().getName());
			newOrderObject.add("drug", drugObj);
		}
		return newOrderObject;
	}
	
	/**
	 * Helper function to add an obs to simpleobject for returning over REST
	 * @param obj
	 * @param order
	 * @return 
	 */
	private SimpleObject createObjectFromObs(Obs obs) {
		SimpleObject newObsObject = new SimpleObject();
		newObsObject.add("uuid", obs.getUuid());
		newObsObject.add("obsDatetime", df.format(obs.getObsDatetime()));
		newObsObject.add("value", obs.getValueAsString(Locale.ENGLISH));
		newObsObject.add("comment", obs.getComment());
		if (obs.getOrder() != null) {
			newObsObject.add("order", obs.getOrder().getUuid());
		} else {
			newObsObject.add("order", null);
		}
		return newObsObject;
	}
	
}
