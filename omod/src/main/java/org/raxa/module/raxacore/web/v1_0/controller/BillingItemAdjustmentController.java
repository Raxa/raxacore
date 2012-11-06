package org.raxa.module.raxacore.web.v1_0.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.BillingItemAdjustment;
import org.raxa.module.raxacore.BillingItemAdjustmentService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the BillingItem resource.
 */

@Controller
@RequestMapping(value = "/rest/v1/raxacore/billingitemadjustment")
public class BillingItemAdjustmentController extends BaseRestController {
	
	BillingItemAdjustmentService service;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "billId", "value", "reason" };
	
	public void initBillingItemAdjustmentController() {
		service = Context.getService(BillingItemAdjustmentService.class);
	}
	
	private String getResourceVersion() {
		return "1.0";
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	@WSDoc("Save BillItemAdjustment")
	@ResponseBody
	public Object saveBillItem(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		initBillingItemAdjustmentController();
		BillingItemAdjustment billItem = new BillingItemAdjustment();
		
		if (post.get("reason") != null) {
			billItem.setReason((post.get("reason").toString()));
		}
		
		if (post.get("billId") != null) {
			billItem.setBillItemId(Integer.parseInt(post.get("billId").toString()));
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
		
		BillingItemAdjustment created;
		SimpleObject obj = obj = new SimpleObject();
		;
		try {
			created = service.saveBillingItemAdjustment(billItem);
			
			obj.add("uuid", created.getUuid());
			obj.add("billId", created.getBillItem());
			
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
	public String getBillItemAdjustmentByUuid(@PathVariable("uuid") String uuid, HttpServletRequest request)
	        throws ResponseException {
		initBillingItemAdjustmentController();
		BillingItemAdjustment billItem = service.getBillingItemAdjustmentByUuid(uuid);
		SimpleObject obj = new SimpleObject();
		obj.add("uuid", billItem.getUuid());
		obj.add("billId", billItem.getBillItemId());
		
		return gson.toJson(obj);
	}
}
