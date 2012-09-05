package org.raxa.module.raxacore;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
import java.io.Serializable;
import java.util.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;

public class RaxaAlert extends BaseOpenmrsData implements Serializable {
	
	private Integer id;
	
	private String name;
	
	private String description;
	
	private Patient patient;
	
	private Integer patientId;
	
	private Boolean seen;
	
	private String alertType;
	
	private Date time;
	
	private String defaultTask;
	
	private Provider providerSent;
	
	private Integer providerSentId;
	
	private Provider providerRecipient;
	
	private Integer toLocationId;
	
	private Location toLocation;
	
	private Integer fromLocationId;
	
	private Location fromLocation;
	
	private Integer providerRecipientId;
	
	public RaxaAlert() {
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
	 * @return the RaxaAlertId
	 */
	@Override
	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Compares two RaxaAlert objects for similarity
	 *
	 * @param obj RaxaAlert object to compare to
	 * @return boolean true/false whether or not they are the same objects
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @should equal RaxaAlert with same raxaAlertID
	 * @should not equal RaxaAlert with different raxaAlertID
	 * @should not equal on null
	 * @should have equal raxaList objects with no raxaAlertIDs
	 * @should not have equal RaxaAlert objects when one has null raxaAlertID
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RaxaAlert) {
			RaxaAlert pList = (RaxaAlert) obj;
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
	 * @return the patientId
	 */
	public Integer getPatientId() {
		return patientId;
	}
	
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	/**
	 * @return the seen
	 */
	public Boolean getSeen() {
		return seen;
	}
	
	/**
	 * @param seen the seen to set
	 */
	public void setSeen(Boolean seen) {
		this.seen = seen;
	}
	
	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}
	
	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param alertType the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	/**
	 * @return the defaultTask
	 */
	public String getDefaultTask() {
		return defaultTask;
	}
	
	/**
	 * @param defaultTask the defaultTask to set
	 */
	public void setDefaultTask(String defaultTask) {
		this.defaultTask = defaultTask;
	}
	
	/**
	 * @return the providerSentId
	 */
	public Integer getProviderSentId() {
		return providerSentId;
	}
	
	/**
	 * @param providerSentId the providerSentId to set
	 */
	public void setProviderSentId(Integer providerSentId) {
		this.providerSentId = providerSentId;
	}
	
	/**
	 * @return the providerRecipientId
	 */
	public Integer getProviderRecipientId() {
		return providerRecipientId;
	}
	
	/**
	 * @param providerRecipientId the providerRecipientId to set
	 */
	public void setProviderRecipientId(Integer providerRecipientId) {
		this.providerRecipientId = providerRecipientId;
	}
	
	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}
	
	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	/**
	 * @return the providerSent
	 */
	public Provider getProviderSent() {
		return providerSent;
	}
	
	/**
	 * @param providerSent the providerSent to set
	 */
	public void setProviderSent(Provider providerSent) {
		this.providerSent = providerSent;
	}
	
	/**
	 * @return the providerRecipient
	 */
	public Provider getProviderRecipient() {
		return providerRecipient;
	}
	
	/**
	 * @param providerRecipient the providerRecipient to set
	 */
	public void setProviderRecipient(Provider providerRecipient) {
		this.providerRecipient = providerRecipient;
	}
	
	/**
	 * This function overrides a function in BaseOpenmrsData just to add JsonIgnore---
	 * The serializer doesn't know what to do with both isVoided() and getVoided()
	 * So we add it here so it ignores getVoided() and uses isVoided()
	 * @return whether this Raxa Alert is voided
	 */
	@Override
	@JsonIgnore
	public Boolean getVoided() {
		return isVoided();
	}
	
	/**
	 * @return the toLocationId
	 */
	public Integer getToLocationId() {
		return toLocationId;
	}
	
	/**
	 * @param toLocationId the toLocationId to set
	 */
	public void setToLocationId(Integer toLocationId) {
		this.toLocationId = toLocationId;
	}
	
	/**
	 * @return the toLocation
	 */
	public Location getToLocation() {
		return toLocation;
	}
	
	/**
	 * @param toLocation the toLocation to set
	 */
	public void setToLocation(Location toLocation) {
		this.toLocation = toLocation;
	}
	
	/**
	 * @return the fromLocationId
	 */
	public Integer getFromLocationId() {
		return fromLocationId;
	}
	
	/**
	 * @param fromLocationId the fromLocationId to set
	 */
	public void setFromLocationId(Integer fromLocationId) {
		this.fromLocationId = fromLocationId;
	}
	
	/**
	 * @return the fromLocation
	 */
	public Location getFromLocation() {
		return fromLocation;
	}
	
	/**
	 * @param fromLocation the fromLocation to set
	 */
	public void setFromLocation(Location fromLocation) {
		this.fromLocation = fromLocation;
	}
	
}
