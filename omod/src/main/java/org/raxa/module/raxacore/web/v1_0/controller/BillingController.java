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
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Billing resource.
 */

@Controller
@Transactional
@RequestMapping(value = "/rest/v1/raxacore/billing")
public class BillingController extends BaseRestController {
	
	BillingService service;
	
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	private static final String[] REF = { "uuid", "patientId", "status", "providerId", "name", "description" };
	
	public void initBillingController() {
		service = Context.getService(BillingService.class);
	}
	
	private String getResourceVersion() {
		return "1.0";
	}
	
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
		return RestUtil.created(response, obj);
		
	}
	
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
}
