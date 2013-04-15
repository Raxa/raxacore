package org.bahmni.module.billing;

public interface BillingService {
	public void createCustomer(String name, String patientId) throws Exception;
    public void updateCustomerBalance(String patientId, double balance) throws Exception;
}
