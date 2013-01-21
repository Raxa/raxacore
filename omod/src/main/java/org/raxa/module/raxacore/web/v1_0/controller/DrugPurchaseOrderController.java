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
		//if it is not a prescription, save it
		//(we don't want prescriptions to show up in purchase order histories -- they will come as orders)
		if (!purchaseOrder.getName().equals(DrugPurchaseOrder.PRESCRIPTIONNAME)) {
			created = service.saveDrugPurchaseOrder(purchaseOrder);
			saveOrUpdateDrugInventories(post, purchaseOrder);
			obj.add("uuid", created.getUuid());
			obj.add("name", created.getName());
			return RestUtil.created(response, obj);
		} else {
			saveOrUpdateDrugInventories(post, purchaseOrder);
			return RestUtil.noContent(response);
		}
	}
	
	/**
	 * Updates the Purchase Order by making a POST call with uuid in URL
	 *
	 * @param uuid the uuid for the purchase order
	 * @param post
	 * @param request
	 * @param response
	 * @return 200 response status
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@WSDoc("Updates an existing purchase order")
	@ResponseBody
	public Object updateDrugPurchaseOrder(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		initDrugPurchaseOrderController();
		DrugPurchaseOrder dPO = service.getDrugPurchaseOrderByUuid(uuid);
		dPO = setPostFields(post, dPO);
		saveOrUpdateDrugInventories(post, dPO);
		DrugPurchaseOrder created = service.updateDrugPurchaseOrder(dPO);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", created.getUuid());
		obj.add("name", created.getName());
		obj.add("description", created.getDescription());
		return RestUtil.noContent(response);
	}
	
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
			purchaseOrder.setReceived(Boolean.parseBoolean(post.get("received").toString()));
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
		if (post.get("dispenseLocation") != null) {
			Location l = Context.getLocationService().getLocationByUuid(post.get("dispenseLocation").toString());
			purchaseOrder.setDispenseLocationId(l.getId());
			purchaseOrder.setDispenseLocation(l);
		}
		if (post.get("stockLocation") != null) {
			Location l = Context.getLocationService().getLocationByUuid(post.get("stockLocation").toString());
			purchaseOrder.setStockLocationId(l.getId());
			purchaseOrder.setStockLocation(l);
		}
		return purchaseOrder;
	}
	
	/**
	 * Helper function to create drug inventories from drug purchase order
	 */
	private void saveOrUpdateDrugInventories(SimpleObject post, DrugPurchaseOrder purchaseOrder) throws ResponseException {
		if (post.get("inventories") != null) {
			List<LinkedHashMap> inventoryObjects = (List<LinkedHashMap>) post.get("inventories");
			//need to differentiate between inventories we are creating, and ones we are updating
			List<DrugInventory> newInventories = new ArrayList();
			List<DrugInventory> updateInventories = new ArrayList();
			List<DrugInventory> updateBatches = new ArrayList();
			for (int i = 0; i < inventoryObjects.size(); i++) {
				//whether or not current inventory is new or an update
				boolean update = false;
				DrugInventory di = new DrugInventory();
				if (inventoryObjects.get(i).get("uuid") != null) {
					System.out.println("getting existing uuid");
					di = Context.getService(DrugInventoryService.class).getDrugInventoryByUuid(
					    inventoryObjects.get(i).get("uuid").toString());
					update = true;
				}
				setDrugInventoryFields(di, inventoryObjects.get(i));
				di.setDrugPurchaseOrder(purchaseOrder);
				di.setDrugPurchaseOrderId(purchaseOrder.getId());
				if (inventoryObjects.get(i).get("batchUuid") != null) {
					DrugInventory batchDrugInv = Context.getService(DrugInventoryService.class).getDrugInventoryByUuid(
					    inventoryObjects.get(i).get("batchUuid").toString());
					if (batchDrugInv == null) {
						throw new ResponseException(
						                            "Batch uuid not found") {};
					}
					if (batchDrugInv.getQuantity() < di.getQuantity()) {
						throw new ResponseException(
						                            "Requested quantity cannot exceed batch quantity") {};
					}
					batchDrugInv.setQuantity(batchDrugInv.getQuantity() - di.getQuantity());
					if (batchDrugInv.getQuantity() == 0)
						batchDrugInv.setStatus("out");
					updateBatches.add(batchDrugInv);
				}
				if (update) {
					updateInventories.add(di);
				} else {
					newInventories.add(di);
				}
			}
			if (!purchaseOrder.getName().equals(DrugPurchaseOrder.PRESCRIPTIONNAME)) {
				for (int n = 0; n < newInventories.size(); n++) {
					Context.getService(DrugInventoryService.class).saveDrugInventory(newInventories.get(n));
				}
				for (int n = 0; n < updateInventories.size(); n++) {
					Context.getService(DrugInventoryService.class).updateDrugInventory(updateInventories.get(n));
				}
			}
			for (int n = 0; n < updateBatches.size(); n++) {
				Context.getService(DrugInventoryService.class).updateDrugInventory(updateBatches.get(n));
			}
			
		}
	}
	
	/**
	 * Helper function to manually set the field for a Drug Inventory this manual setting should be done with a
	 * Resource, however a bug exists in OpenMRS: https://tickets.openmrs.org/browse/TRUNK-2205
	 */
	private void setDrugInventoryFields(DrugInventory drugInventory, LinkedHashMap postFields) throws ResponseException {
		if (postFields.get("name") != null) {
			drugInventory.setName(postFields.get("name").toString());
		}
		if (postFields.get("description") != null) {
			drugInventory.setDescription(postFields.get("description").toString());
		}
		if (postFields.get("drug") != null) {
			Drug d = Context.getConceptService().getDrugByUuid(postFields.get("drug").toString());
			if (d == null) {
				throw new ResponseException(
				                            "Drug uuid not found") {};
			}
			drugInventory.setDrugId(d.getDrugId());
			drugInventory.setDrug(d);
		}
		if (postFields.get("quantity") != null) {
			drugInventory.setQuantity(Integer.parseInt(postFields.get("quantity").toString()));
		}
		if (postFields.get("originalQuantity") != null) {
			drugInventory.setOriginalQuantity(Integer.parseInt(postFields.get("originalQuantity").toString()));
		}
		if (postFields.get("expiryDate") != null) {
			Date date = null;
			String[] supportedFormats = { "EEE MMM dd yyyy HH:mm:ss z (zzzz)", "EEE MMM dd yyyy HH:mm:ss z (zzzz)",
			        "MMM dd, yyyy HH:mm:ss a", "EEE MMM dd yyyy HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
			        "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
			        "yyyy-MM-dd" };
			for (int i = 0; i < supportedFormats.length; i++) {
				try {
					date = new SimpleDateFormat(supportedFormats[i]).parse(postFields.get("expiryDate").toString());
					drugInventory.setExpiryDate(date);
				}
				catch (Exception ex) {}
			}
			if (date == null) {
				throw new ResponseException(
				                            "Invalid date " + postFields.get("expiryDate")) {};
			}
		}
		if (postFields.get("batch") != null) {
			drugInventory.setBatch(postFields.get("batch").toString());
		}
		if (postFields.get("supplier") != null) {
			drugInventory.setSupplier(postFields.get("supplier").toString());
		}
		if (postFields.get("roomLocation") != null) {
			drugInventory.setRoomLocation(postFields.get("roomLocation").toString());
		}
		if (postFields.get("value") != null) {
			drugInventory.setValue(Integer.parseInt(postFields.get("value").toString()));
		}
		if (postFields.get("status") != null) {
			drugInventory.setStatus(postFields.get("status").toString());
		}
		if (postFields.get("provider") != null) {
			Provider p = Context.getProviderService().getProviderByUuid(postFields.get("provider").toString());
			if (p == null) {
				throw new ResponseException(
				                            "Provider uuid not found") {};
			}
			drugInventory.setProviderId(p.getId());
			drugInventory.setProvider(p);
		}
		if (postFields.get("location") != null) {
			Location l = Context.getLocationService().getLocationByUuid(postFields.get("location").toString());
			if (l == null) {
				throw new ResponseException(
				                            "Location uuid not found") {};
			}
			drugInventory.setLocationId(l.getId());
			drugInventory.setLocation(l);
		}
		if (postFields.get("drugPurchaseOrder") != null) {
			DrugPurchaseOrder dPOrder = Context.getService(DrugPurchaseOrderService.class).getDrugPurchaseOrderByUuid(
			    postFields.get("drugPurchaseOrder").toString());
			if (dPOrder == null) {
				throw new ResponseException(
				                            "DrugPurchaseOrder uuid not found") {};
			}
			drugInventory.setDrugPurchaseOrderId(dPOrder.getId());
			drugInventory.setDrugPurchaseOrder(dPOrder);
		}
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
	 * Fetch Drug Purchase Orders according to stockLocation
	 *
	 * @param stockLocation
	 * @param request
	 * @param response
	 * @return drug purchase orders for the given stockLocation
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "stockLocation")
	@WSDoc("Fetch all non-retired drug purchase orders according to stockLocation")
	@ResponseBody()
	public String searchByStockLocation(@RequestParam("stockLocation") String stockLocation, HttpServletRequest request)
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
			pObj.add("display", p.getName());
		}
		obj.add("provider", pObj);
		obj.add("date", dpo.getDrugPurchaseOrderDate());
		SimpleObject dispenseObj = new SimpleObject();
		Location dispenseLoc = dpo.getDispenseLocation();
		if (dispenseLoc != null) {
			dispenseObj.add("uuid", dispenseLoc.getUuid());
			dispenseObj.add("display", dispenseLoc.getName());
		}
		obj.add("dispenseLocation", dispenseObj);
		SimpleObject stockObj = new SimpleObject();
		Location stockLoc = dpo.getStockLocation();
		if (stockLoc != null) {
			stockObj.add("uuid", stockLoc.getUuid());
			stockObj.add("display", stockLoc.getName());
		}
		obj.add("stockLocation", stockObj);
		//getting all associated drug inventories:
		List<DrugInventory> inventories = Context.getService(DrugInventoryService.class)
		        .getDrugInventoriesByDrugPurchaseOrder(dpo.getId());
		if (!inventories.isEmpty()) {
			ArrayList invObjs = new ArrayList();
			//List<SimpleObject> invObjs = new ArrayList();
			for (int i = 0; i < inventories.size(); i++) {
				SimpleObject newInvObj = new SimpleObject();
				newInvObj.add("name", inventories.get(i).getName());
				newInvObj.add("description", inventories.get(i).getDescription());
				newInvObj.add("uuid", inventories.get(i).getUuid());
				SimpleObject drugObj = new SimpleObject();
				Drug d = inventories.get(i).getDrug();
				if (d != null) {
					drugObj.add("uuid", d.getUuid());
					drugObj.add("display", d.getName());
				}
				newInvObj.add("drug", drugObj);
				newInvObj.add("quantity", inventories.get(i).getQuantity());
				newInvObj.add("originalQuantity", inventories.get(i).getOriginalQuantity());
				newInvObj.add("expiryDate", inventories.get(i).getExpiryDate());
				newInvObj.add("batch", inventories.get(i).getBatch());
				newInvObj.add("supplier", inventories.get(i).getSupplier());
				newInvObj.add("roomLocation", inventories.get(i).getRoomLocation());
				newInvObj.add("value", inventories.get(i).getValue());
				newInvObj.add("status", inventories.get(i).getStatus());
				SimpleObject providerObj = new SimpleObject();
				Provider provider = inventories.get(i).getProvider();
				if (provider != null) {
					providerObj.add("uuid", provider.getUuid());
					providerObj.add("display", provider.getName());
				}
				newInvObj.add("provider", providerObj);
				SimpleObject locObj = new SimpleObject();
				Location l = inventories.get(i).getLocation();
				if (l != null) {
					locObj.add("uuid", l.getUuid());
					locObj.add("display", l.getName());
				}
				newInvObj.add("location", locObj);
				invObjs.add(newInvObj);
			}
			obj.add("inventories", invObjs);
		}
		return obj;
	}
}
