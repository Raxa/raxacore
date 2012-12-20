package org.raxa.module.raxacore.web.v1_0.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingItem;
import org.raxa.module.raxacore.BillingItemAdjustment;
import org.raxa.module.raxacore.BillingItemAdjustmentService;
import org.raxa.module.raxacore.BillingItemService;
import org.raxa.module.raxacore.BillingService;
import org.raxa.module.raxacore.impl.BillingServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the BillingItem resource.
 */

@Controller
@Transactional
@RequestMapping(value = "/rest/v1/raxacore/billingitem")
public class BillingItemController extends BaseRestController {
	
	BillingItemService service;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "billId", "conceptId", "providerId", "encounterId", "orderId" };
	
	public void initBillingItemController() {
		service = Context.getService(BillingItemService.class);
	}
	
	private String getResourceVersion() {
		return "1.0";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save BillItem")
	@ResponseBody
	public Object saveBillItem(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initBillingItemController();
		BillingItem billItem = new BillingItem();
		//drugInventory.setName(post.get("name").toString());
		//drugInventory.setDescription(post.get("description").toString());
		//drugInventory.setUuid(post.get("uuid").toString());
		
		if (post.get("billId") != null) {
			billItem.setBillId(Integer.parseInt(post.get("billId").toString()));
		}
		if (post.get("conceptId") != null) {
			
			billItem.setConceptId(Integer.parseInt(post.get("conceptId").toString()));
			billItem.setConcept(Context.getConceptService().getConcept(Integer.parseInt(post.get("conceptId").toString())));
			
		}
		if (post.get("encounterId") != null) {
			billItem.setEncounterId(Integer.parseInt(post.get("encounterId").toString()));
			billItem.setEncounter(Context.getEncounterService().getEncounter(
			    Integer.parseInt(post.get("encounterId").toString())));
			
		}
		if (post.get("providerId") != null) {
			billItem.setProviderId(Integer.parseInt(post.get("providerId").toString()));
			billItem.setProvider(Context.getProviderService().getProvider(
			    Integer.parseInt(post.get("providerId").toString())));
			
		}
		
		if (post.get("orderId") != null) {
			billItem.setOrderId(Integer.parseInt(post.get("orderId").toString()));
			billItem.setOrder(Context.getOrderService().getOrder(Integer.parseInt(post.get("orderId").toString())));
			
		}
		
		if (post.get("quantity") != null) {
			billItem.setQuantity(Integer.parseInt(post.get("quantity").toString()));
		}
		
		if (post.get("value") != null) {
			billItem.setValue(Integer.parseInt(post.get("value").toString()));
		}
		
		if (post.get("name") != null) {
			billItem.setName(post.get("name").toString());
		}
		
		if (post.get("description") != null) {
			billItem.setDescription(post.get("description").toString());
		}
		
		BillingItem created;
		SimpleObject obj = obj = new SimpleObject();
		;
		try {
			created = service.saveBillingItem(billItem);
			
			obj.add("uuid", created.getUuid());
			obj.add("billId", created.getBillId());
			obj.add("providerId", created.getProviderId());
			obj.add("orderId", created.getOrderId());
			obj.add("encounterId", created.getEncounterId());
			obj.add("conceptId", created.getConceptId());
		}
		catch (Exception e) {
			System.out.println("helllloooooo errroorr ocuuured");
			e.printStackTrace();
		}
		return RestUtil.created(response, obj);
		
	}
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets bill by the uuid path")
	@ResponseBody()
	public String getBillItemByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request) throws ResponseException {
		initBillingItemController();
		BillingItem billItem = service.getBillingItemByUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", billItem.getUuid());
		obj.add("billId", billItem.getBillId());
		obj.add("conceptId", billItem.getConceptId());
		obj.add("orderId", billItem.getOrderId());
		obj.add("encounterId", billItem.getEncounterId());
		obj.add("providerId", billItem.getProviderId());
		
		return gson.toJson(obj);
	}
	
	/**
	 *Gets all billingItems and its adjustments given Bill Id  
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@WSDoc("Gets All Biiling Items by Bill Id")
	@ResponseBody()
	public String getPatientListsByName(@RequestParam("q") Integer query, HttpServletRequest request)
	        throws ResponseException {
		initBillingItemController();
		
		List<BillingItem> getAllBillingItemsByBill = service.getAllBillingItemsByBill(query);
		ArrayList results = new ArrayList();
		for (BillingItem patientList : getAllBillingItemsByBill) {
			SimpleObject obj = new SimpleObject();
			
			List<BillingItemAdjustment> adjust = Context.getService(BillingItemAdjustmentService.class)
			        .getAllBillingItemAdjustmentsByBillingItem(patientList.getbillItemId());
			
			for (BillingItemAdjustment adj : adjust) {
				obj.add("Olddiscount", adj.getValue());
				obj.add("OlddiscountReason", adj.getReason());
			}
			
			obj.add("Olduuid", patientList.getUuid());
			obj.add("OldbillItemId", patientList.getbillItemId());
			obj.add("Oldquantity", patientList.getQuantity());
			obj.add("Oldname", patientList.getName());
			obj.add("Olddescription", patientList.getDescription());
			obj.add("OldproviderId", patientList.getProviderId());
			
			obj.add("Oldvalue", patientList.getValue());
			obj.add("OlddateCreated", patientList.getDateCreated());
			
			results.add(obj);
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
}
