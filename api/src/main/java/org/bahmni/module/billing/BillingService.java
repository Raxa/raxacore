package org.bahmni.module.billing;

public interface BillingService {
	public void createCustomer(String name, String patientId, String village);
    public void updateCustomerBalance(String patientId, double balance);
    Object[] findCustomers(String patientId);
}
