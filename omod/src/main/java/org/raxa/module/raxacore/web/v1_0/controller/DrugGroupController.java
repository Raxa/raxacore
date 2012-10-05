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
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the DrugGroup resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/druggroup")
public class DrugGroupController extends BaseRestController {
	
	DrugGroupService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "name", "description" };
	
	public void initDrugGroupController() {
		service = Context.getService(DrugGroupService.class);
	}
	
	//<editor-fold defaultstate="collapsed" desc="getResourceVersion">
	/**
	 * Returns the Resource Version
	 */
	private String getResourceVersion() {
		return "1.0";
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="POST - Without Params">
	/**
	 * Create new drug group by POST'ing atleast name and description property
	 * in the request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and DrugGroup object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New DrugGroup")
	@ResponseBody
	public Object createNewDrugGroup(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setName(post.get("name").toString());
		drugGroup.setDescription(post.get("description").toString());
		DrugGroup created = service.saveDrugGroup(drugGroup);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return RestUtil.created(response, obj);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="POST - Update DrugGroup">
	/**
	 * Updates the Drug Group by making a POST call with uuid in URL and
	 *
	 * @param uuid the uuid for the drug group resource
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing drug group")
	@ResponseBody
	public Object updateDrugGroup(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = service.getDrugGroupByUuid(uuid);
		drugGroup.setName(post.get("name").toString());
		drugGroup.setDescription(post.get("description").toString());
		DrugGroup created = service.updateDrugGroup(drugGroup);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return RestUtil.noContent(response);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="GET all">
	/**
	 * Get all the unretired drug groups (as REF representation) in the system
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Drug Groups in the system")
	@ResponseBody()
	public String getAllDrugGroups(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugGroupController();
		List<DrugGroup> allDrugGroup = service.getAllDrugGroup(false);
		ArrayList results = new ArrayList();
		for (DrugGroup drugGroup : allDrugGroup) {
			SimpleObject obj = new SimpleObject();
			obj.add("uuid", drugGroup.getUuid());
			obj.add("name", drugGroup.getName());
			obj.add("description", drugGroup.getDescription());
			results.add(obj);
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="GET - Search by name">
	/**
	 * Search DrugGroup by Name and get the resource as REF representation
	 *
	 * @param query the string to search name of drugGroup
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@WSDoc("Gets Drug Groups by name")
	@ResponseBody()
	public String getDrugGroupsByName(@RequestParam("q") String query, HttpServletRequest request) throws ResponseException {
		initDrugGroupController();
		List<DrugGroup> allDrugGroup = service.getDrugGroupByName(query);
		ArrayList results = new ArrayList();
		for (DrugGroup drugGroup : allDrugGroup) {
			SimpleObject obj = new SimpleObject();
			obj.add("uuid", drugGroup.getUuid());
			obj.add("name", drugGroup.getName());
			obj.add("description", drugGroup.getDescription());
			results.add(obj);
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="GET by uuid - DEFAULT REP">
	/**
	 * Get the DrugGroup along with drugs, encounters and obs (DEFAULT rep).
	 * Contains all encounters of the searched encounterType between the
	 * startDate and endDate
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets Drug Groups for the uuid path")
	@ResponseBody()
	public String getAllDrugGroupByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = service.getDrugGroupByUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", drugGroup.getUuid());
		obj.add("name", drugGroup.getName());
		obj.add("description", drugGroup.getDescription());
		ArrayList drugs = new ArrayList();
		List<Drug> drugsInDrugGroup = new ArrayList<Drug>(drugGroup.getDrugs());
		for (Drug p : drugsInDrugGroup) {
			SimpleObject drug = new SimpleObject();
			drug.add("uuid", p.getUuid());
			drugs.add(drug);
		}
		obj.add("drugs", drugs);
		return gson.toJson(obj);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="GET by uuid - FULL REP">
	/**
	 * Get the drug group as FULL representation that shows drugs,
	 * encounters and obs. Contains all encounters of the searched encounterType
	 * between the startDate and endDate. Contains drugGroup.searchQuery,
	 * encounter.provider and obs.comment and obs.order compared to DEFAULT rep
	 *
	 * @param uuid
	 * @param rep
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, params = "v")
	@WSDoc("Gets Full representation of Drug Groups for the uuid path")
	@ResponseBody()
	public String getAllDrugGroupByUuidFull(@PathVariable("uuid") String uuid, @RequestParam("v") String rep,
	        HttpServletRequest request) throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = service.getDrugGroupByUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", drugGroup.getUuid());
		obj.add("name", drugGroup.getName());
		obj.add("description", drugGroup.getDescription());
		ArrayList drugs = new ArrayList();
		List<Drug> drugsInDrugGroup = new ArrayList<Drug>(drugGroup.getDrugs());
		for (Drug p : drugsInDrugGroup) {
			SimpleObject drug = new SimpleObject();
			drug.add("uuid", p.getUuid());
			drugs.add(drug);
		}
		obj.add("drugs", drugs);
		if (rep.equals("full")) {
			obj.add("retired", drugGroup.getRetired());
			if (drugGroup.getRetired()) {
				obj.add("retiredBy", drugGroup.getRetiredBy().getUuid());
				obj.add("retireReason", drugGroup.getRetireReason());
			}
			SimpleObject auditInfo = new SimpleObject();
			auditInfo.add("creator", drugGroup.getCreator().getUuid());
			auditInfo.add("dateCreated", df.format(drugGroup.getDateCreated()));
			if (drugGroup.getChangedBy() != null) {
				auditInfo.add("changedBy", drugGroup.getChangedBy().getUuid());
				auditInfo.add("dateChanged", df.format(drugGroup.getDateChanged()));
			}
			obj.add("auditInfo", auditInfo);
		}
		obj.add("resourceVersion", getResourceVersion());
		return gson.toJson(obj);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="DELETE - Retire DrugGroup">
	/**
	 * Retires the drug group resource by making a DELETE call with the
	 * '!purge' param
	 *
	 * @param uuid
	 * @param reason
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@WSDoc("Retires the Drug Group")
	@ResponseBody
	public Object retireDrugGroup(@PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = service.getDrugGroupByUuid(uuid);
		if (drugGroup != null) {
			drugGroup.setRetired(true);
			drugGroup.setRetireReason(reason);
			drugGroup.setRetiredBy(Context.getAuthenticatedUser());
			service.updateDrugGroup(drugGroup);
		}
		return RestUtil.noContent(response);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="DELETE - Purge DrugGroup">
	/**
	 * Purges (Complete Delete) the drug group resource by making a DELETE
	 * call and passing the 'purge' param
	 *
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purgeDrugGroup(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugGroupController();
		DrugGroup drugGroup = service.getDrugGroupByUuid(uuid);
		if (drugGroup != null) {
			service.deleteDrugGroup(drugGroup);
		}
		return RestUtil.noContent(response);
	}
	//</editor-fold>
}
