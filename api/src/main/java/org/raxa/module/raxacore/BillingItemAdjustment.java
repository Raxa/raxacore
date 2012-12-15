package org.raxa.module.raxacore;

/*
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
 * BillingItemAdjustments  stores the data of billingitems in the bill like discounts , reason of discount .
 */

public class BillingItemAdjustment extends BaseOpenmrsMetadata implements Serializable {
	
	private Integer billItemAdjustmentId;
	
	private Integer billItemId;
	
	private String reason;
	
	private Integer value;
	
	private BillingItem billItem;
	
	public BillingItemAdjustment() {
		
	}
	
	public Integer getBillItemAdjustmentId() {
		return billItemAdjustmentId;
	}
	
	public void setBillItemAdjustmentId(Integer billItemAdjustmentId) {
		this.billItemAdjustmentId = billItemAdjustmentId;
	}
	
	public Integer getBillItemId() {
		return billItemId;
	}
	
	public void setBillItemId(Integer billItemId) {
		this.billItemId = billItemId;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public BillingItem getBillItem() {
		return billItem;
	}
	
	public void setBillItem(BillingItem billitem) {
		this.billItem = billitem;
	}
	
	@Override
	public Integer getId() {
		return getBillItemAdjustmentId();
		
	}
	
	@Override
	public void setId(Integer arg0) {
		setBillItemAdjustmentId(arg0);
		
	}
	
}
