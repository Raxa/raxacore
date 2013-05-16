package org.bahmni.module.billing;

public interface BillingService {
	public void createCustomer(String name, String patientId);
    public void updateCustomerBalance(String patientId, double balance);
    Object[] findCustomers(String patientId);
}
