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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;
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
@RequestMapping(value = "/rest/v1/raxacore/drug")
public class RaxaDrugController extends BaseRestController {
	
	ConceptService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "name", "description" };
	
	public void initDrugController() {
		service = Context.getConceptService();
	}
	
	/**
	 * Returns the Resource Version
	 */
	private String getResourceVersion() {
		return "1.0";
	}
	
	/**
	 * Create new drug by POST'ing at least name and description property in the
	 * request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and Drug object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New Drug")
	@ResponseBody
	public Object createNewDrug(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugController();
		String conceptUuid = post.get("concept").toString();
		Concept concept = service.getConceptByUuid(conceptUuid);
		
		if (concept == null) {
			throw new ObjectNotFoundException();
		}
		
		Drug drug = new Drug();
		drug.setConcept(concept);
		updateDrugFieldsFromPostData(drug, post);
		Drug drugJustCreated = service.saveDrug(drug);
		if (post.get("drugInfo") != null) {
			this.createNewDrugInfo(drugJustCreated, (LinkedHashMap) post.get("drugInfo"));
		}
		return RestUtil.created(response, getDrugAsSimpleObject(drugJustCreated));
	}
	
	/**
	 * Updates the Drug by making a POST call with uuid in URL and
	 *
	 * @param uuid the uuid for the drug resource
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing drug")
	@ResponseBody
	public Object updateDrug(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initDrugController();
		Drug drug = service.getDrugByUuid(uuid);
		updateDrugFieldsFromPostData(drug, post);
		service.saveDrug(drug);
		return RestUtil.created(response, getDrugAsSimpleObject(drug));
	}
	
	/**
	 * Updates attributes of a Drug copying them from a SimpleObject
	 *
	 * @param drug
	 * @param obj
	 */
	private void updateDrugFieldsFromPostData(Drug drug, SimpleObject obj) {
		if (obj.get("name") != null) {
			drug.setName(obj.get("name").toString());
		}
		if (obj.get("description") != null) {
			drug.setDescription(obj.get("description").toString());
		}
		if (obj.get("combination") != null) {
			drug.setCombination(Boolean.parseBoolean(obj.get("combination").toString()));
		}
		if (obj.get("maximumDailyDose") != null) {
			drug.setMaximumDailyDose(Double.parseDouble(obj.get("maximumDailyDose").toString()));
		}
		if (obj.get("minimumDailyDose") != null) {
			drug.setMinimumDailyDose(Double.parseDouble(obj.get("minimumDailyDose").toString()));
		}
		if (obj.get("dosageForm") != null) {
			drug.setDosageForm(Context.getConceptService().getConceptByUuid(obj.get("dosageForm").toString()));
		}
		if (obj.get("units") != null) {
			drug.setUnits(obj.get("units").toString());
		}
	}
	
	/**
	 * Creates a drug info for the given drug
	 */
	private void createNewDrugInfo(Drug drug, LinkedHashMap drugInfoMap) {
		String drugUuid = drug.getUuid();
		
		// create drug info POJO and add required relationship with a Drug
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		if (drugInfoMap.get("name") != null) {
			drugInfo.setName(drugInfoMap.get("name").toString());
		}
		if (drugInfoMap.get("description") != null) {
			drugInfo.setDescription(drugInfoMap.get("description").toString());
		}
		if (drugInfoMap.get("price") != null) {
			drugInfo.setPrice(Double.parseDouble(drugInfoMap.get("price").toString()));
		}
		if (drugInfoMap.get("cost") != null) {
			drugInfo.setCost(Double.parseDouble(drugInfoMap.get("cost").toString()));
		}
		// save new object and prepare response
		DrugInfo drugInfoJustCreated = Context.getService(DrugInfoService.class).saveDrugInfo(drugInfo);
	}
	
	/**
	 * Returns a SimpleObject containing some fields of Drug
	 *
	 * @param drug
	 * @return
	 */
	private SimpleObject getDrugAsSimpleObject(Drug drug) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", drug.getUuid());
		obj.add("name", drug.getName());
		obj.add("description", drug.getDescription());
		obj.add("minimumDailyDose", drug.getMinimumDailyDose());
		obj.add("maximumDailyDose", drug.getMaximumDailyDose());
		if (drug.getDosageForm() != null) {
			obj.add("dosageForm", drug.getDosageForm().getName().getName());
		}
		obj.add("strength", drug.getDoseStrength());
		obj.add("units", drug.getUnits());
		obj.add("combination", drug.getCombination());
		obj.add("concept", drug.getConcept().getUuid());
		obj.add("fullName", drug.getFullName(Context.getLocale()));
		return obj;
	}
	
	/**
	 * Get all the unretired drug (as REF representation) in the system
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Drug in the system")
	@ResponseBody()
	public String getAllDrugs(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugController();
		List<Drug> allDrug = service.getAllDrugs(false);
		ArrayList results = new ArrayList();
		for (Drug drug : allDrug) {
			results.add(getDrugAsSimpleObject(drug));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Get the Drug
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Drug for the uuid path")
	@ResponseBody()
	public String getDrugByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request) throws ResponseException {
		initDrugController();
		Drug drug = service.getDrugByUuid(uuid);
		return gson.toJson(getDrugAsSimpleObject(drug));
	}
	
	/**
	 * Get the drug as FULL representation
	 *
	 * @param uuid
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, params = "v")
	@WSDoc("Gets Full representation of Drug for the uuid path")
	@ResponseBody()
	public String getDrugByUuidFull(@PathVariable("uuid") String uuid, @RequestParam("v") String rep,
	        HttpServletRequest request) throws ResponseException {
		initDrugController();
		Drug drug = service.getDrugByUuid(uuid);
		SimpleObject obj = getDrugAsSimpleObject(drug);
		if (rep.equals("full")) {
			obj.add("retired", drug.getRetired());
			if (drug.getRetired()) {
				obj.add("retiredBy", drug.getRetiredBy().getUuid());
				obj.add("retireReason", drug.getRetireReason());
			}
			SimpleObject auditInfo = new SimpleObject();
			auditInfo.add("creator", drug.getCreator().getUuid());
			auditInfo.add("dateCreated", df.format(drug.getDateCreated()));
			if (drug.getChangedBy() != null) {
				auditInfo.add("changedBy", drug.getChangedBy().getUuid());
				auditInfo.add("dateChanged", df.format(drug.getDateChanged()));
			}
			obj.add("auditInfo", auditInfo);
		}
		obj.add("resourceVersion", getResourceVersion());
		return gson.toJson(obj);
	}
	
	/**
	 * Retires the drug resource by making a DELETE call with the '!purge' param
	 *
	 * @param uuid
	 * @param reason
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@WSDoc("Retires the Drug")
	@ResponseBody
	public Object retireDrug(@PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initDrugController();
		Drug drug = service.getDrugByUuid(uuid);
		if (drug != null) {
			drug.setRetired(true);
			drug.setRetireReason(reason);
			drug.setRetiredBy(Context.getAuthenticatedUser());
			service.updateDrug(drug);
		}
		return RestUtil.noContent(response);
	}
}
