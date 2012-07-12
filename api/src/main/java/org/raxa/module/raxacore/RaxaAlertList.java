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
import org.openmrs.BaseOpenmrsData;

public class RaxaAlertList extends BaseOpenmrsMetadata implements Serializable {

	private Integer raxaAlertId;

    private Patient patient;
    
    private Integer patientId;
    
    private DateTime time;
    
    private Set<Obs> obs;
    
    private String defaultTask;
    
    private Provider providerSent;
    
    private Integer providerSentId;
    
    private Provider providerRecipient;

	private Integer providerRecipientId;
    
    public RaxaAlertList() {
	}
	
	/** Sets id
	 * 
	 * @param id: id to set
	 */
	@Override
	public void setId(Integer id) {
		setRaxaAlertListId(id);
	}
	
	/** Gets id
	 * 
	 * @return the RaxaAlertListId
	 */
	@Override
	public Integer getId() {
		return getRaxaAlertListId();
	}
	
	/** Compares two RaxaAlertList objects for similarity
	 * 
	 * @param obj RaxaAlertList object to compare to
	 * @return boolean true/false whether or not they are the same objects
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @should equal RaxaAlertList with same raxaAlertID
	 * @should not equal RaxaAlertList with different raxaAlertID
	 * @should not equal on null
	 * @should have equal patientList objects with no raxaAlertIDs
	 * @should not have equal RaxaAlertList objects when one has null raxaAlertID
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RaxaAlertList) {
			RaxaAlertList pList = (RaxaAlertList) obj;
			if (this.getRaxaAlertListId() != null && pList.getRaxaAlertListId() != null)
				return (this.getRaxaAlertListId().equals(pList.getRaxaAlertListId()));
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
		if (this.getRaxaAlertListId() == null)
			return super.hashCode();
		return this.getRaxaAlertListId().hashCode();
	}
	
	/**
	 * @return the raxaAlertId
	 */
	public Integer getRaxaAlertListId() {
		return raxaAlertId;
	}
	
	/**
	 * @param raxaAlertID the raxaAlertID to set
	 */
	public void setRaxaAlertListId(Integer raxaAlertId) {
		this.raxaAlertId = raxaAlertId;
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
	 * @return the time
	 */
	public DateTime getTime() {
		return time;
	}
	
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
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
	 * @return the providerSent
	 */
	public Provider getProviderSent() {
		return providerSent;
	}
	
	/**
	 * @param providerSent the providerSent to set
	 */
	public void setProviderSent(String providerSent) {
		this.providerSent = providerSent;
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
	 * @return the providerRecipient
	 */
	public Provider getProviderRecipient() {
		return providerSent;
	}
	
	/**
	 * @param providerRecipient the providerRecipient to set
	 */
	public void setProviderRecipient(Provider providerRecipient) {
		this.providerRecipient = providerRecipient;
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
