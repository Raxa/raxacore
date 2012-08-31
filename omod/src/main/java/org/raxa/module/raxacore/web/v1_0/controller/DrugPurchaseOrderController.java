package org.raxa.module.raxacore.web.v1_0.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Drug;
import org.openmrs.Location;

import org.openmrs.Provider;
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
 * Controller for REST web service access to the DrugPurchaseOrder resource.
 */
@Controller
@RequestMapping(value = "/rest/v1/raxacore/drugpurchaseorder")
public class DrugPurchaseOrderController extends BaseRestController {
	
	DrugPurchaseOrderService service;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "name", "providerId" };
	
	public void initDrugPurchaseOrderController() {
		service = Context.getService(DrugPurchaseOrderService.class);
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
	 * Create new drug purchase order by POST'ing atleast name and providerId property in the request body.
	 *
	 * @param post the body of the POST request
	 * @param request
	 * @param response
	 * @return 201 response status and DrugPurchaseOrder object
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save New DrugPurchaseOrder")
	@ResponseBody
	public Object createNewDrugPurchaseOrder(@RequestBody SimpleObject post, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		initDrugPurchaseOrderController();
		
		DrugPurchaseOrder purchaseOrder = setPostFields(post, new DrugPurchaseOrder());
		DrugPurchaseOrder created;
		SimpleObject obj = obj = new SimpleObject();
		created = service.saveDrugPurchaseOrder(purchaseOrder);
		saveOrUpdateDrugInventories(post, purchaseOrder);
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		return RestUtil.created(response, obj);
	}
	
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="GET by uuid - DEFAULT REP">
	/**
	 * Get the DrugPurchaseOrder
	 *
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets DrugPurchaseOrder for the uuid path")
	@ResponseBody()
	public String getDrugPuchaseOrderByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initDrugPurchaseOrderController();
		DrugPurchaseOrder drugOrder = service.getDrugPurchaseOrderByUuid(uuid);
		SimpleObject obj = this.getFieldsFromDrugPurchaseOrder(drugOrder);
		return gson.toJson(obj);
	}
	
	/**
	 * Helper function to get fields from POST and put into purchaseOrder
	 */
	public DrugPurchaseOrder setPostFields(SimpleObject post, DrugPurchaseOrder purchaseOrder) {
		if (post.get("name") != null) {
			purchaseOrder.setName(post.get("name").toString());
		}
		if (post.get("description") != null) {
			purchaseOrder.setDescription(post.get("description").toString());
		}
		if (post.get("received") != null) {
			purchaseOrder.setReceived(Boolean.getBoolean(post.get("received").toString()));
		}
		if (post.get("drugPurchaseOrderDate") != null) {
			String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
			        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
			for (int i = 0; i < supportedFormats.length; i++) {
				try {
					Date date = new SimpleDateFormat(supportedFormats[i]).parse(post.get("expiryDate").toString());
					purchaseOrder.setDrugPurchaseOrderDate(date);
				}
				catch (Exception ex) {}
			}
		}
		if (post.get("provider") != null) {
			Provider p = Context.getProviderService().getProviderByUuid(post.get("provider").toString());
			purchaseOrder.setProviderId(p.getId());
			purchaseOrder.setProvider(p);
		}
		if (post.get("dispenselocation") != null) {
			Location l = Context.getLocationService().getLocationByUuid(post.get("dispenselocation").toString());
			purchaseOrder.setDispenseLocationId(l.getId());
			purchaseOrder.setDispenseLocation(l);
		}
		if (post.get("stocklocation") != null) {
			Location l = Context.getLocationService().getLocationByUuid(post.get("stocklocation").toString());
			purchaseOrder.setStockLocationId(l.getId());
			purchaseOrder.setStockLocation(l);
		}
		return purchaseOrder;
	}
	
	/**
	 * Helper function to create drug inventories from drug purchase order
	 */
	private void saveOrUpdateDrugInventories(SimpleObject post, DrugPurchaseOrder purchaseOrder) {
		if (post.get("inventories") != null) {
			String s = post.get("inventories").toString();
			s = s.replaceAll("[\\[\\]]", "");
			String[] strings = s.split("}");
			for (int i = 0; i < strings.length; i++) {
				String currString = strings[i];
				if (currString.indexOf("{") != -1) {
					DrugInventory di = new DrugInventory();
					currString = currString.replaceAll("[,]*[\\s]*[\\{]", "");
					String[] keyValuePairs = currString.split(",");
					for (int j = 0; j < keyValuePairs.length; j++) {
						String[] currentPair = keyValuePairs[j].split("=");
						if (currentPair.length == 2) {
							di = setDrugInventoryField(di, currentPair[0].trim(), currentPair[1].trim());
						}
					}
					Context.getService(DrugInventoryService.class).saveDrugInventory(di);
				}
			}
		}
	}
	
	/**
	 * Helper function to manually set the field for a Drug Inventory this manual setting should be done with a
	 * Resource, however a bug exists in OpenMRS: https://tickets.openmrs.org/browse/TRUNK-2205
	 */
	private DrugInventory setDrugInventoryField(DrugInventory drugInventory, String key, String value) {
		if (key.equals("name")) {
			drugInventory.setName(value);
		}
		if (key.equals("description")) {
			drugInventory.setDescription(value);
		}
		if (key.equals("drug")) {
			Drug d = Context.getConceptService().getDrugByUuid(value);
			drugInventory.setDrugId(d.getDrugId());
			drugInventory.setDrug(d);
		}
		if (key.equals("quantity")) {
			drugInventory.setQuantity(Integer.parseInt(value));
		}
		if (key.equals("originalQuantity")) {
			drugInventory.setOriginalQuantity(Integer.parseInt(value));
		}
		if (key.equals("expiryDate")) {
			String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
			        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
			for (int i = 0; i < supportedFormats.length; i++) {
				try {
					Date date = new SimpleDateFormat(supportedFormats[i]).parse(value);
					drugInventory.setExpiryDate(date);
				}
				catch (Exception ex) {}
			}
		}
		if (key.equals("batch")) {
			drugInventory.setBatch(value);
		}
		if (key.equals("value")) {
			drugInventory.setValue(Integer.parseInt(value));
		}
		if (key.equals("status")) {
			drugInventory.setStatus(value);
		}
		if (key.equals("provider")) {
			Provider p = Context.getProviderService().getProviderByUuid(value);
			drugInventory.setProviderId(p.getId());
			drugInventory.setProvider(p);
		}
		if (key.equals("location")) {
			Location l = Context.getLocationService().getLocationByUuid(value);
			drugInventory.setLocationId(l.getId());
			drugInventory.setLocation(l);
		}
		if (key.equals("drugPurchaseOrder")) {
			DrugPurchaseOrder dPO = Context.getService(DrugPurchaseOrderService.class).getDrugPurchaseOrderByUuid(value);
			drugInventory.setDrugPurchaseOrderId(dPO.getId());
			drugInventory.setDrugPurchaseOrder(dPO);
		}
		return drugInventory;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get All Unretired Drug Purchase Orders in the system")
	@ResponseBody()
	public String getAllDrugPurchaseOrders(HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initDrugPurchaseOrderController();
		List<DrugPurchaseOrder> allDPOs = service.getAllDrugPurchaseOrders();
		return purchaseOrderListToJson(allDPOs);
	}
	
	/**
	 * Fetch Drug Purchase Orders according to stocklocation
	 *
	 * @param stocklocation
	 * @param request
	 * @param response
	 * @return drug purchase orders for the given stocklocation
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "stocklocation")
	@WSDoc("Fetch all non-retired drug purchase orders according to stocklocation")
	@ResponseBody()
	public String searchByStockLocation(@RequestParam("stocklocation") String stockLocation, HttpServletRequest request)
	        throws ResponseException {
		initDrugPurchaseOrderController();
		List<DrugPurchaseOrder> dPOs = service.getDrugPurchaseOrderByStockLocation(Context.getLocationService()
		        .getLocationByUuid(stockLocation).getId());
		return purchaseOrderListToJson(dPOs);
	}
	
	/**
	 * Helper function that parses a list of Inventories, returns a JSon
	 */
	private String purchaseOrderListToJson(List<DrugPurchaseOrder> drugPurchaseOrders) {
		ArrayList results = new ArrayList();
		for (DrugPurchaseOrder dpo : drugPurchaseOrders) {
			results.add(getFieldsFromDrugPurchaseOrder(dpo));
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Helper function to return Drug Purchase Order to front end
	 *
	 * @param dpo
	 * @return SimpleObject the representation of Drug Inventory
	 */
	private SimpleObject getFieldsFromDrugPurchaseOrder(DrugPurchaseOrder dpo) {
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", dpo.getUuid());
		obj.add("name", dpo.getName());
		obj.add("description", dpo.getDescription());
		obj.add("received", dpo.isReceived());
		SimpleObject pObj = new SimpleObject();
		Provider p = dpo.getProvider();
		if (p != null) {
			pObj.add("uuid", p.getUuid());
			pObj.add("dposplay", p.getName());
		}
		obj.add("provider", pObj);
		obj.add("date", dpo.getDrugPurchaseOrderDate());
		SimpleObject dispenseObj = new SimpleObject();
		Location dispenseLoc = dpo.getDispenseLocation();
		if (dispenseLoc != null) {
			dispenseObj.add("uuid", dispenseLoc.getUuid());
			dispenseObj.add("display", dispenseLoc.getName());
		}
		obj.add("dispenselocation", dispenseObj);
		SimpleObject stockObj = new SimpleObject();
		Location stockLoc = dpo.getStockLocation();
		if (stockLoc != null) {
			stockObj.add("uuid", stockLoc.getUuid());
			stockObj.add("display", stockLoc.getName());
		}
		obj.add("stocklocation", stockObj);
		return obj;
	}
}
