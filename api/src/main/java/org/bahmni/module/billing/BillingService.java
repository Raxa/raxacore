package org.bahmni.module.billing;

import org.openmrs.api.PatientService;

public interface BillingService {
	
	public void createCustomer(String name, String patientId);
}
