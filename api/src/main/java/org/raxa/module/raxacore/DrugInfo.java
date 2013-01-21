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
import org.openmrs.Drug;

/**
 * DrugInfo stores the data needed to lists patients for registration, screener,
 * etc.
 */
public class DrugInfo extends BaseOpenmrsMetadata implements Serializable {
	
	private Integer id;
	
	private Integer drugId;
	
	private Drug drug;
	
	private double price;
	
	private double cost;
	
	private String shortName;
	
	private String brandName;
	
	private String supplier;
	
	private String manufacturer;
	
	private Integer reorderLevel;
	
	private String note;
	
	public DrugInfo() {
	}
	
	/**
	 * Sets id
	 *
	 * @param id: id to set
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets id
	 *
	 * @return the drugInfoId
	 */
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * Compares two DrugInfo objects for similarity
	 *
	 * @param obj DrugInfo object to compare to
	 * @return boolean true/false whether or not they are the same objects
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @should equal DrugInfo with same drugInfoId
	 * @should not equal DrugInfo with different drugInfoId
	 * @should not equal on null
	 * @should have equal drugInfo objects with no drugInfoIds
	 * @should not have equal DrugInfo objects when one has null drugInfoId
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DrugInfo) {
			DrugInfo pList = (DrugInfo) obj;
			if (this.getId() != null && pList.getId() != null) {
				return (this.getId().equals(pList.getId()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @should have same hashcode when equal
	 * @should have different hash code when not equal
	 * @should get hash code with null attributes
	 */
	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}
		return this.getId().hashCode();
	}
	
	/**
	 * @return the drug
	 */
	public Drug getDrug() {
		return drug;
	}
	
	/**
	 * @param drug the drug to set
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}
	
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}
	
	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * @return the drugId
	 */
	public Integer getDrugId() {
		return drugId;
	}
	
	/**
	 * @param drugId the drugId to set
	 */
	public void setDrugId(Integer drugId) {
		this.drugId = drugId;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	/**
	 * @return the brandName
	 */
	public String getBrandName() {
		return brandName;
	}
	
	/**
	 * @param brandName the brandName to set
	 */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}
	
	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	/**
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	
	/**
	 * @param manufacturer the manufacturer to set
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	/**
	 * @return the reorderLevel
	 */
	public Integer getReorderLevel() {
		return reorderLevel;
	}
	
	/**
	 * @param reorderLevel the reorderLevel to set
	 */
	public void setReorderLevel(Integer reorderLevel) {
		this.reorderLevel = reorderLevel;
	}
}
