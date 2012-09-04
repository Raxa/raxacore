package org.raxa.module.raxacore;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.io.Serializable;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Provider;

/**
 * BillingItem stores the data of items in the bill.
 */
public class BillingItem extends BaseOpenmrsMetadata implements Serializable {
	
	private Integer billItemId;
	
	private Integer billId;
	
	private Integer providerId;
	
	private Integer conceptId;
	
	private Integer encounterId;
	
	private Integer orderId;
	
	private Integer quantity;
	
	private Integer value;
	
	private Provider provider;
	
	private Concept concept;
	
	private Encounter encounter;
	
	private Order order;
	
	private Billing bill;
	
	public BillingItem() {
		
	}
	
	public Integer getbillItemId() {
		return billItemId;
	}
	
	public void setbillItemId(Integer billItemId) {
		this.billItemId = billItemId;
	}
	
	public Integer getBillId() {
		return billId;
	}
	
	public void setBillId(Integer billId) {
		this.billId = billId;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Integer getProviderId() {
		return providerId;
	}
	
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	public Concept getConcept() {
		return concept;
	}
	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public Order getOrder() {
		return order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public Billing getBill() {
		return bill;
	}
	
	public void setBill(Billing bill) {
		this.bill = bill;
	}
	
	@Override
	public Integer getId() {
		return getbillItemId();
	}
	
	@Override
	public void setId(Integer arg0) {
		setbillItemId(arg0);
	}
	
}
