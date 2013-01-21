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
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Drug;
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
 * Controller for REST web service access to the DrugInfo resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/druginfo")
public class DrugInfoController extends BaseRestController {
	
	DrugInfoService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "name", "description", "price", "cost" };
	
	public void initDrugInfoController() {
		service = Context.getService(DrugInfoService.class);
	}
	
	/**
	 * Returns the Resource Version
	 */
	private String getResourceVersion() {
		return "1.0";
	}
	
	/**
	 * Create new drug info by POST'ing atleast name and description property in
	 * the request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and DrugInfo object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New DrugInfo")
	@ResponseBody
	public Object createNewDrugInfo(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugInfoController();
		
		String drugUuid = post.get("drug").toString();
		Drug drug = Context.getConceptService().getDrugByUuid(drugUuid);
		
		if (drug == null) {
			// drug doesn't exist, so we won't create the drug info
			throw new ObjectNotFoundException();
		}
		
		// create drug info POJO and add required relationship with a Drug
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		
		// add data that was sent in the POST payload
		updateDrugInfoFieldsFromPostData(drugInfo, post);
		
		// save new object and prepare response
		DrugInfo drugInfoJustCreated = service.saveDrugInfo(drugInfo);
		
		return RestUtil.created(response, getDrugInfoAsSimpleObject(drugInfoJustCreated));
	}
	
	/**
	 * Updates the Drug Info by making a POST call with uuid in URL and
	 *
	 * @param uuid the uuid for the drug info resource
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing drug info")
	@ResponseBody
	public Object updateDrugInfo(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugInfoController();
		DrugInfo drugInfo = service.getDrugInfoByUuid(uuid);
		updateDrugInfoFieldsFromPostData(drugInfo, post);
		service.updateDrugInfo(drugInfo);
		return RestUtil.created(response, getDrugInfoAsSimpleObject(drugInfo));
	}
	
	/**
	 * Updates attributes of a DrugInfo copying them from a SimpleObject
	 *
	 * @param drugInfo
	 * @param obj
	 */
	private void updateDrugInfoFieldsFromPostData(DrugInfo drugInfo, SimpleObject obj) {
		if (obj.get("name") != null) {
			drugInfo.setName(obj.get("name").toString());
		}
		if (obj.get("description") != null) {
			drugInfo.setDescription(obj.get("description").toString());
		}
		if (obj.get("price") != null) {
			drugInfo.setPrice(Double.parseDouble(obj.get("price").toString()));
		}
		if (obj.get("cost") != null) {
			drugInfo.setCost(Double.parseDouble(obj.get("cost").toString()));
		}
		if (obj.get("reorderLevel") != null) {
			drugInfo.setReorderLevel(Integer.parseInt(obj.get("reorderLevel").toString()));
		}
		if (obj.get("supplier") != null) {
			drugInfo.setSupplier((obj.get("supplier").toString()));
		}
		if (obj.get("manufacturer") != null) {
			drugInfo.setManufacturer((obj.get("manufacturer").toString()));
		}
		if (obj.get("shortName") != null) {
			drugInfo.setShortName(obj.get("shortName").toString());
		}
		if (obj.get("brandName") != null) {
			drugInfo.setBrandName(obj.get("brandName").toString());
		}
	}
	
	/**
	 * Returns a SimpleObject containing some fields of DrugInfo
	 *
	 * @param drugInfo
	 * @return
	 */
	private SimpleObject getDrugInfoAsSimpleObject(DrugInfo drugInfo) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", drugInfo.getUuid());
		obj.add("name", drugInfo.getName());
		obj.add("drugUuid", drugInfo.getDrug().getUuid());
		obj.add("drugName", drugInfo.getDrug().getName());
		obj.add("description", drugInfo.getDescription());
		obj.add("shortName", drugInfo.getShortName());
		obj.add("brandName", drugInfo.getBrandName());
		obj.add("supplier", drugInfo.getSupplier());
		obj.add("manufacturer", drugInfo.getManufacturer());
		obj.add("price", drugInfo.getPrice());
		obj.add("reorderLevel", drugInfo.getReorderLevel());
		obj.add("cost", drugInfo.getCost());
		return obj;
	}
	
	/**
	 * Get all the unvoided drug info (as REF representation) in the system
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unvoided Drug Info in the system")
	@ResponseBody()
	public String getAllDrugInfo(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugInfoController();
		List<DrugInfo> allDrugInfo = service.getAllDrugInfo(false);
		ArrayList results = new ArrayList();
		for (DrugInfo drugInfo : allDrugInfo) {
			results.add(getDrugInfoAsSimpleObject(drugInfo));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Get the DrugInfo
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Drug Info for the uuid path")
	@ResponseBody()
	public String getAllDrugInfoByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initDrugInfoController();
		DrugInfo drugInfo = service.getDrugInfoByUuid(uuid);
		return gson.toJson(getDrugInfoAsSimpleObject(drugInfo));
	}
	
	/**
	 * Get the drug info as FULL representation
	 *
	 * @param uuid
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, params = "v")
	@WSDoc("Gets Full representation of Drug Info for the uuid path")
	@ResponseBody()
	public String getAllDrugInfoByUuidFull(@PathVariable("uuid") String uuid, @RequestParam("v") String rep,
	        HttpServletRequest request) throws ResponseException {
		initDrugInfoController();
		DrugInfo drugInfo = service.getDrugInfoByUuid(uuid);
		SimpleObject obj = getDrugInfoAsSimpleObject(drugInfo);
		if (rep.equals("full")) {
			obj.add("retired", drugInfo.getRetired());
			if (drugInfo.getRetired()) {
				obj.add("retiredBy", drugInfo.getRetiredBy().getUuid());
				obj.add("retireReason", drugInfo.getRetireReason());
			}
			SimpleObject auditInfo = new SimpleObject();
			auditInfo.add("creator", drugInfo.getCreator().getUuid());
			auditInfo.add("dateCreated", df.format(drugInfo.getDateCreated()));
			if (drugInfo.getChangedBy() != null) {
				auditInfo.add("changedBy", drugInfo.getChangedBy().getUuid());
				auditInfo.add("dateChanged", df.format(drugInfo.getDateChanged()));
			}
			obj.add("auditInfo", auditInfo);
		}
		obj.add("resourceVersion", getResourceVersion());
		return gson.toJson(obj);
	}
	
	/**
	 * Search Druginfo by drug name
	 * 
	 * @param query the name to search for specific drug
	 * @param request
	 * @return
	 * @throws ResponseException 
	 */
	@RequestMapping(method = RequestMethod.GET, params = "drugname")
	@WSDoc("Gets drug info by drug name")
	@ResponseBody()
	public String getDrugInfosByName(@RequestParam("drugname") String query, HttpServletRequest request)
	        throws ResponseException {
		initDrugInfoController();
		List<DrugInfo> allDrugInfos = service.getDrugInfosByDrugName(query);
		ArrayList results = new ArrayList();
		for (DrugInfo drugInfo : allDrugInfos) {
			if (drugInfo != null) {
				results.add(getDrugInfoAsSimpleObject(drugInfo));
			}
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Voids the drug info resource by making a DELETE call with the '!purge'
	 * param
	 *
	 * @param uuid
	 * @param reason
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@WSDoc("Retires the Drug Info")
	@ResponseBody
	public Object retireDrugInfo(@PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initDrugInfoController();
		DrugInfo drugInfo = service.getDrugInfoByUuid(uuid);
		if (drugInfo != null) {
			drugInfo.setRetired(true);
			drugInfo.setRetireReason(reason);
			drugInfo.setRetiredBy(Context.getAuthenticatedUser());
			service.updateDrugInfo(drugInfo);
		}
		return RestUtil.noContent(response);
	}
	
	/**
	 * Purges (Complete Delete) the drug info resource by making a DELETE call
	 * and passing the 'purge' param
	 *
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purgeDrugInfo(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugInfoController();
		DrugInfo drugInfo = service.getDrugInfoByUuid(uuid);
		if (drugInfo != null) {
			service.deleteDrugInfo(drugInfo);
		}
		return RestUtil.noContent(response);
	}
}
