package org.raxa.module.raxacore.web.v1_0.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Encounter;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
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
import org.raxa.module.raxacore.PatientList;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Billing resource.
 */

@Controller
@Transactional
@RequestMapping(value = "/rest/v1/raxacore/billing")
public class BillingController extends BaseRestController {
	
	BillingService service;
	
	EncounterService serve;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "patientId", "status", "providerId", "name", "description", "billId",
	        "dateCreated", "balance", "totalAmount", "encounterId", "category", "item_name", "quantity", "price",
	        "doscountReason" };
	
	public void initBillingController() {
		service = Context.getService(BillingService.class);
		serve = Context.getEncounterService();
	}
	
	private String getResourceVersion() {
		return "1.0";
	}
	
	/**
	 * 
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 * Saves a bill along with all billing items and ajustments 
	 */
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save Bill")
	@ResponseBody
	public Object saveBill(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initBillingController();
		Billing bill = new Billing();
		
		bill.setPatientId(Integer.parseInt(post.get("patientId").toString()));
		bill.setProviderId(Integer.parseInt(post.get("providerId").toString()));
		bill.setStatus(post.get("status").toString());
		
		bill.setBalance(Integer.parseInt(post.get("balance").toString()));
		
		bill.setTotalAmount(Integer.parseInt(post.get("totalAmount").toString()));
		
		if (post.get("name") != null) {
			bill.setName(post.get("name").toString());
		}
		
		if (post.get("description") != null) {
			bill.setDescription(post.get("description").toString());
		}
		
		bill.setProvider(Context.getProviderService().getProvider(Integer.parseInt(post.get("providerId").toString())));
		
		bill.setPatient(Context.getPatientService().getPatient(Integer.parseInt(post.get("patientId").toString())));
		
		Billing created;
		SimpleObject obj = new SimpleObject();
		
		try {
			created = service.saveBill(bill);
			
			obj.add("uuid", created.getUuid());
			obj.add("patientId", created.getPatientId());
			obj.add("providerId", created.getProviderId());
			obj.add("status", created.getStatus());
		}
		catch (Exception e) {
			System.out.println(" error");
			e.printStackTrace();
		}
		saveOrUpdateBillItem(post, bill);
		return RestUtil.created(response, obj);
		
	}
	
	/**
	 * 
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 gets Billing Item by uuid
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@WSDoc("Gets bill by the uuid path")
	@ResponseBody()
	public String getBillByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request) throws ResponseException {
		initBillingController();
		Billing bill = service.getBillByPatientUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", bill.getUuid());
		obj.add("patientId", bill.getPatientId());
		obj.add("providerId", bill.getProviderId());
		obj.add("status", bill.getStatus());
		return gson.toJson(obj);
	}
	
	/**
	 * 
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 gets all bills by patientID
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@WSDoc("Gets All bills by patientId")
	@ResponseBody()
	public String getPatientListsByName(@RequestParam("q") Integer query, HttpServletRequest request)
	        throws ResponseException {
		initBillingController();
		
		List<Billing> getAllBillsByPatient = service.getAllBillsByPatient(query);
		ArrayList results = new ArrayList();
		for (Billing patientList : getAllBillsByPatient) {
			SimpleObject obj = new SimpleObject();
			obj.add("uuid", patientList.getUuid());
			obj.add("billId", patientList.getBillId());
			obj.add("status", patientList.getStatus());
			
			obj.add("providerId", patientList.getProviderId());
			obj.add("dateCreated", patientList.getDateCreated());
			obj.add("balance", patientList.getBalance());
			obj.add("totalAmount", patientList.getTotalAmount());
			
			results.add(obj);
		}
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * 
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 gets all encounters  by patientID
	 */
	@RequestMapping(method = RequestMethod.GET, params = "v")
	@WSDoc("Gets All Encounters  by patientId")
	@ResponseBody()
	public String getEncountersByPatientId(@RequestParam("v") Integer query, HttpServletRequest request)
	        throws ResponseException {
		initBillingController();
		
		List<Encounter> all = serve.getEncountersByPatientId(query);
		ArrayList results = new ArrayList();
		for (Encounter patientList : all) {
			SimpleObject obj = new SimpleObject();
			//	obj.add("uuid", patientList.getUuid());
			obj.add("item_name", "EncounterId:" + patientList.getEncounterId().toString());
			//	obj.add("providerId", patientList.getProvidersByRoles());
			obj.add("discountReason", patientList.getDateCreated());
			
			if (patientList.getEncounterType().getEncounterTypeId().toString().compareTo("1") == 0) {
				obj.add("category", "ADULTINITIAL");
			}
			if (patientList.getEncounterType().getEncounterTypeId().toString().compareTo("2") == 0) {
				obj.add("category", "ADULTRETURN");
			}

			else if (patientList.getEncounterType().getEncounterTypeId().toString().compareTo("5") == 0) {
				obj.add("category", "OUTPATIENT");
			} else if (patientList.getEncounterType().getEncounterTypeId().toString().compareTo("6") == 0) {
				obj.add("category", "REGISTRATION");
			} else if (patientList.getEncounterType().getEncounterTypeId().toString().compareTo("7") == 0) {
				obj.add("category", "PRESCRIPTION");
			}
			
			obj.add("quantity", "1");
			obj.add("price", "500");
			
			results.add(obj);
			
		}
		
		return gson.toJson(new SimpleObject().add("results", results));
	}
	
	/**
	 * Saves billing Items in bill
	 * @param post
	 * @param bill
	 * @throws ResponseException
	 */
	private void saveOrUpdateBillItem(SimpleObject post, Billing bill) throws ResponseException {
		
		if (post.get("billItems") != null) {
			List<LinkedHashMap> billItemObjects = (List<LinkedHashMap>) post.get("billItems");
			for (int i = 0; i < billItemObjects.size(); i++)

			{
				
				BillingItem billItem = new BillingItem();
				
				//setBillItemFields(billItem, billItemObjects.get(i));
				billItem.setBillId(bill.getBillId());
				if (billItemObjects.get(i).get("quantity") != null) {
					billItem.setQuantity(Integer.parseInt(billItemObjects.get(i).get("quantity").toString()));
				}
				
				if (billItemObjects.get(i).get("value") != null) {
					billItem.setValue(Integer.parseInt(billItemObjects.get(i).get("value").toString()));
				}
				
				if (billItemObjects.get(i).get("name") != null) {
					billItem.setName(billItemObjects.get(i).get("name").toString());
				}
				
				if (billItemObjects.get(i).get("description") != null) {
					billItem.setDescription(billItemObjects.get(i).get("description").toString());
				}
				
				Context.getService(BillingItemService.class).saveBillingItem(billItem);
				
				BillingItemAdjustment adjust = new BillingItemAdjustment();
				adjust.setBillItemId(billItem.getbillItemId());
				
				if (billItemObjects.get(i).get("discount") != null) {
					adjust.setValue(Integer.parseInt(billItemObjects.get(i).get("discount").toString()));
				}
				
				if (billItemObjects.get(i).get("discountReason") != null) {
					adjust.setReason(billItemObjects.get(i).get("discountReason").toString());
				}
				Context.getService(BillingItemAdjustmentService.class).saveBillingItemAdjustment(adjust);
				
			}
			
		}
		
	}
	
}
