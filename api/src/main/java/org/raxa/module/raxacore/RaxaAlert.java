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
import java.util.*;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;

public class RaxaAlert extends BaseOpenmrsData implements Serializable {
	
	private Integer raxaAlertId;
	
	private String name;
	
	private String description;
	
	private Patient patient;
	
	private Integer patientId;
	
	private Boolean seen;
	
	private String alertType;
	
	private Date time;
	
	private Set<Obs> obs;
	
	private String defaultTask;
	
	private Provider providerSent;
	
	private Integer providerSentId;
	
	private Provider providerRecipient;
	
	private Integer providerRecipientId;
	
	public RaxaAlert() {
	}
	
	/** Sets id
	 * 
	 * @param id: id to set
	 */
	@Override
	public void setId(Integer id) {
		setRaxaAlertId(id);
	}
	
	/** Gets id
	 * 
	 * @return the RaxaAlertId
	 */
	@Override
	public Integer getId() {
		return getRaxaAlertId();
	}
	
	/** Compares two RaxaAlert objects for similarity
	 * 
	 * @param obj RaxaAlert object to compare to
	 * @return boolean true/false whether or not they are the same objects
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @should equal RaxaAlert with same raxaAlertID
	 * @should not equal RaxaAlert with different raxaAlertID
	 * @should not equal on null
	 * @should have equal patientList objects with no raxaAlertIDs
	 * @should not have equal RaxaAlert objects when one has null raxaAlertID
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RaxaAlert) {
			RaxaAlert pList = (RaxaAlert) obj;
			if (this.getRaxaAlertId() != null && pList.getRaxaAlertId() != null)
				return (this.getRaxaAlertId().equals(pList.getRaxaAlertId()));
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
		if (this.getRaxaAlertId() == null)
			return super.hashCode();
		return this.getRaxaAlertId().hashCode();
	}
	
	/**
	 * @return the raxaAlertId
	 */
	public Integer getRaxaAlertId() {
		return raxaAlertId;
	}
	
	/**
	 * @param raxaAlertID the raxaAlertID to set
	 */
	public void setRaxaAlertId(Integer raxaAlertId) {
		this.raxaAlertId = raxaAlertId;
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
	public Boolean seen() {
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
	 * @return Returns a Set<Obs> of all non-voided, non-obsGroup children Obs of this Alert
	 * @should not return null with null obs set
	 * @should get obs
	 * @should not get voided obs
	 * @should only get child obs
	 * @should not get child obs if child also on Alert
	 * @should get both child and parent obs after removing child from parent grouping
	 * @should get obs with two levels of hierarchy
	 * @should get obs with three levels of hierarchy
	 * @should not get voided obs with three layers of hierarchy
	 */
	public Set<Obs> getObs() {
		Set<Obs> ret = new HashSet<Obs>();
		if (this.obs != null) {
			for (Obs o : this.obs)
				ret.addAll(getObsLeaves(o));
			// this should be all thats needed unless the alert has been built by hand
			//if (o.isVoided() == false && o.isObsGrouping() == false)
			//      ret.add(o);
		}
		return ret;
	}
	
	/**
	 * Convenience method to recursively get all leaf obs of this Alert. This method goes down
	 * into each obs and adds all non-grouping obs to the return list
	 * 
	 * @param obsParent current obs to loop over
	 * @return list of leaf obs
	 */
	private List<Obs> getObsLeaves(Obs obsParent) {
		List<Obs> leaves = new ArrayList<Obs>();
		if (obsParent.hasGroupMembers()) {
			for (Obs child : obsParent.getGroupMembers()) {
				if (child.isVoided() == false) {
					if (child.isObsGrouping() == false)
						leaves.add(child);
					else
						// recurse if this is a grouping obs
						leaves.addAll(getObsLeaves(child));
				}
			}
		} else if (obsParent.isVoided() == false) {
			leaves.add(obsParent);
		}
		return leaves;
	}
	
	/**
	 * Returns all Obs where Obs.encounterId = Encounter.encounterId In practice, this method should
	 * not be used very often...
	 *
	 * @param includeVoided specifies whether or not to include voided Obs
	 * @return Returns the all Obs.
	 * @should not return null with null obs set
	 * @should get obs
	 * @should get both parent and child obs
	 * @should get both parent and child with child directly
	 * @should get both child and parent obs after removing child from parent grouping
	 */
	public Set<Obs> getAllObs(boolean includeVoided) {
		if (includeVoided && obs != null)
			return obs;
		Set<Obs> ret = new HashSet<Obs>();
		if (this.obs != null) {
			for (Obs o : this.obs) {
				if (includeVoided)
					ret.add(o);
				else if (!o.isVoided())
					ret.add(o);
			}
		}
		return ret;
	}
	
	/**
	 * Returns a Set<Obs> of all root-level Obs of an Alert, including obsGroups
	 * 
	 * @param includeVoided specifies whether or not to include voided Obs
	 * @return Returns all obs at top level -- will not be null
	 * @should not return null with null obs set
	 * @should get obs
	 * @should not get voided obs
	 * @should only get parents obs
	 * @should only return the grouped top level obs
	 * @should get both child and parent obs after removing child from parent grouping
	 */
	public Set<Obs> getObsAtTopLevel(boolean includeVoided) {
		Set<Obs> ret = new HashSet<Obs>();
		for (Obs o : getAllObs(includeVoided)) {
			if (o.getObsGroup() == null)
				ret.add(o);
		}
		return ret;
	}
	
	/**
	 * @param obs The obs to set.
	 */
	public void setObs(Set<Obs> obs) {
		this.obs = obs;
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
	
}
