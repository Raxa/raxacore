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
import java.util.Date;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Location;
import org.openmrs.Provider;

public class DrugPurchaseOrder extends BaseOpenmrsMetadata implements Serializable {
	
	public static final String ISSUENAME = "Pharmacy Issue";
	
	public static final String RECEIPTNAME = "Pharmacy Receipt";
	
	public static final String REQUISITIONNAME = "Pharmacy Requisition";
	
	public static final String PRESCRIPTIONNAME = "Prescription";
	
	private Integer id;
	
	private boolean received;
	
	private Integer providerId;
	
	private Integer dispenseLocationId;
	
	private Integer stockLocationId;
	
	private Date drugPurchaseOrderDate;
	
	private Provider provider;
	
	private Location dispenseLocation;
	
	private Location stockLocation;
	
	public DrugPurchaseOrder() {
		
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer arg0) {
		this.id = arg0;
	}
	
	public boolean isReceived() {
		return received;
	}
	
	public void setReceived(boolean received) {
		this.received = received;
	}
	
	public Integer getProviderId() {
		return providerId;
	}
	
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	
	public Date getDrugPurchaseOrderDate() {
		return drugPurchaseOrderDate;
	}
	
	public void setDrugPurchaseOrderDate(Date drugPurchaseOrderDate) {
		this.drugPurchaseOrderDate = drugPurchaseOrderDate;
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	/**
	 * @return the dispenseLocationId
	 */
	public Integer getDispenseLocationId() {
		return dispenseLocationId;
	}
	
	/**
	 * @param dispenseLocationId the dispenseLocationId to set
	 */
	public void setDispenseLocationId(Integer dispenseLocationId) {
		this.dispenseLocationId = dispenseLocationId;
	}
	
	/**
	 * @return the stockLocationId
	 */
	public Integer getStockLocationId() {
		return stockLocationId;
	}
	
	/**
	 * @param stockLocationId the stockLocationId to set
	 */
	public void setStockLocationId(Integer stockLocationId) {
		this.stockLocationId = stockLocationId;
	}
	
	/**
	 * @return the dispenseLocation
	 */
	public Location getDispenseLocation() {
		return dispenseLocation;
	}
	
	/**
	 * @param dispenseLocation the dispenseLocation to set
	 */
	public void setDispenseLocation(Location dispenseLocation) {
		this.dispenseLocation = dispenseLocation;
	}
	
	/**
	 * @return the stockLocation
	 */
	public Location getStockLocation() {
		return stockLocation;
	}
	
	/**
	 * @param stockLocation the stockLocation to set
	 */
	public void setStockLocation(Location stockLocation) {
		this.stockLocation = stockLocation;
	}
	
}
