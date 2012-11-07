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
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.DrugInventory;
import org.raxa.module.raxacore.DrugInventoryService;
import org.raxa.module.raxacore.DrugPurchaseOrder;
import org.raxa.module.raxacore.DrugPurchaseOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Drug Inventory resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/druginventory")
public class DrugInventoryController extends BaseRestController {
	
	DrugInventoryService service;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "drugId", "quantity" };
	
	public void initDrugInventoryController() {
		service = Context.getService(DrugInventoryService.class);
	}
	
	private String getResourceVersion() {
		return "1.0";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save DrugInventory")
	@ResponseBody
	public Object saveDrugInventory(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugInventoryController();
		DrugInventory drugInventory = setPostFields(post, new DrugInventory());
		DrugInventory created;
		SimpleObject obj = obj = new SimpleObject();
		created = service.saveDrugInventory(drugInventory);
		obj.add("uuid", created.getUuid());
		obj.add("drugId", created.getDrugId());
		obj.add("quantity", created.getQuantity());
		return RestUtil.created(response, obj);
	}
	
	/**
	 * Helper function to get fields from POST and put into DrugInventory
	 */
	public DrugInventory setPostFields(SimpleObject post, DrugInventory drugInventory) {
		if (post.get("name") != null) {
			drugInventory.setName(post.get("name").toString());
		}
		if (post.get("description") != null) {
			drugInventory.setDescription(post.get("description").toString());
		}
		if (post.get("drug") != null) {
			Drug d = Context.getConceptService().getDrugByUuid(post.get("drug").toString());
			drugInventory.setDrugId(d.getDrugId());
			drugInventory.setDrug(d);
		}
		if (post.get("quantity") != null) {
			drugInventory.setQuantity(Integer.parseInt(post.get("quantity").toString()));
		}
		if (post.get("originalQuantity") != null) {
			drugInventory.setOriginalQuantity(Integer.parseInt(post.get("originalQuantity").toString()));
		}
		if (post.get("expiryDate") != null) {
			String[] supportedFormats = { "EEE MMM dd yyyy HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
			        "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
			        "yyyy-MM-dd" };
			for (int i = 0; i < supportedFormats.length; i++) {
				try {
					Date date = new SimpleDateFormat(supportedFormats[i]).parse(post.get("expiryDate").toString());
					drugInventory.setExpiryDate(date);
				}
				catch (Exception ex) {}
			}
		}
		if (post.get("batch") != null) {
			drugInventory.setBatch(post.get("batch").toString());
		}
		if (post.get("supplier") != null) {
			drugInventory.setSupplier(post.get("supplier").toString());
		}
		if (post.get("value") != null) {
			drugInventory.setValue(Integer.parseInt(post.get("value").toString()));
		}
		if (post.get("status") != null) {
			drugInventory.setStatus(post.get("status").toString());
		}
		if (post.get("roomLocation") != null) {
			drugInventory.setRoomLocation(post.get("roomLocation").toString());
		}
		if (post.get("provider") != null) {
			Provider p = Context.getProviderService().getProviderByUuid(post.get("provider").toString());
			drugInventory.setProviderId(p.getId());
			drugInventory.setProvider(p);
		}
		if (post.get("location") != null) {
			Location l = Context.getLocationService().getLocationByUuid(post.get("location").toString());
			drugInventory.setLocationId(l.getId());
			drugInventory.setLocation(l);
		}
		if (post.get("drugPurchaseOrder") != null) {
			DrugPurchaseOrder dPO = Context.getService(DrugPurchaseOrderService.class).getDrugPurchaseOrderByUuid(
			    post.get("drugPurchaseOrder").toString());
			drugInventory.setDrugPurchaseOrderId(dPO.getId());
			drugInventory.setDrugPurchaseOrder(dPO);
		}
		return drugInventory;
	}
	
	/**
	 * Helper function to return Drug Inventory to front end
	 *
	 * @param di
	 * @return SimpleObject the representation of Drug Inventory
	 */
	private SimpleObject getFieldsFromDrugInventory(DrugInventory di) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", di.getUuid());
		obj.add("name", di.getName());
		obj.add("description", di.getDescription());
		SimpleObject drugObj = new SimpleObject();
		Drug d = di.getDrug();
		if (d != null) {
			drugObj.add("uuid", d.getUuid());
			drugObj.add("display", d.getName());
			if (d.getDosageForm() != null) {
				drugObj.add("dosageForm", d.getDosageForm().getName().getName());
			}
		}
		obj.add("drug", drugObj);
		obj.add("quantity", di.getQuantity());
		obj.add("originalQuantity", di.getOriginalQuantity());
		obj.add("expiryDate", di.getExpiryDate());
		obj.add("batch", di.getBatch());
		obj.add("supplier", di.getSupplier());
		obj.add("value", di.getValue());
		obj.add("status", di.getStatus());
		obj.add("roomLocation", di.getRoomLocation());
		SimpleObject pObj = new SimpleObject();
		Provider p = di.getProvider();
		if (p != null) {
			pObj.add("uuid", p.getUuid());
			pObj.add("display", p.getName());
		}
		obj.add("provider", pObj);
		SimpleObject lObj = new SimpleObject();
		Location l = di.getLocation();
		if (l != null) {
			lObj.add("uuid", l.getUuid());
			lObj.add("display", l.getName());
		}
		obj.add("location", lObj);
		SimpleObject dPOObj = new SimpleObject();
		DrugPurchaseOrder dPO = di.getDrugPurchaseOrder();
		if (dPO != null) {
			dPOObj.add("uuid", dPO.getUuid());
			dPOObj.add("display", dPO.getName());
		}
		obj.add("drugPurchaseOrder", dPOObj);
		return obj;
	}
	
	/**
	 * Gets drug inventory by uuid
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets drug inventory for the uuid path")
	@ResponseBody()
	public String getDrugInventoryByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initDrugInventoryController();
		DrugInventory drugInventory = service.getDrugInventoryByUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", drugInventory.getUuid());
		obj.add("drugId", drugInventory.getDrugId());
		obj.add("quantity", drugInventory.getQuantity());
		return gson.toJson(obj);
	}
	
	/**
	 * Updates the Drug Inventory by making a POST call with uuid in URL
	 *
	 * @param uuid the uuid for the drug inventory resource
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing drug inventory")
	@ResponseBody
	public Object updateDrugInventory(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugInventoryController();
		DrugInventory di = service.getDrugInventoryByUuid(uuid);
		di = setPostFields(post, di);
		DrugInventory created = service.updateDrugInventory(di);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return gson.toJson(obj);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Drug Inventories in the system")
	@ResponseBody()
	public String getAllDrugInventories(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugInventoryController();
		List<DrugInventory> allDIs = service.getAllDrugInventories();
		ArrayList results = new ArrayList();
		for (DrugInventory di : allDIs) {
			results.add(getFieldsFromDrugInventory(di));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Helper function that parses a list of Inventories, returns a JSon
	 */
	private String inventoryListToJson(List<DrugInventory> drugInventories) {
		ArrayList results = new ArrayList();
		for (DrugInventory di : drugInventories) {
			results.add(getFieldsFromDrugInventory(di));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Fetch Drug Inventories according to location
	 *
	 * @param location
	 * @param request
	 * @param response
	 * @return drug inventories for the given location
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "location")
	@WSDoc("Fetch all non-retired inventories according to location")
	@ResponseBody()
	public String searchByLocation(@RequestParam("location") String location, HttpServletRequest request)
	        throws ResponseException {
		initDrugInventoryController();
		List<DrugInventory> dIs = service.getDrugInventoriesByLocation(Context.getLocationService().getLocationByUuid(
		    location).getId());
		return inventoryListToJson(dIs);
	}
}
