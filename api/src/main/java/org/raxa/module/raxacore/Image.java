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
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.util.OpenmrsUtil;

/**
 * Image stores the data needed for images
 */
public class Image extends BaseOpenmrsData implements Serializable {
	
	private Integer id;
		
	private String tags;
	
    private String fileName;
    
    private Patient patient;

    private Integer patientId;
		
	private Provider provider;

    private Integer providerId;

    private Location location;
    
    private Integer locationId;

    private byte[] imageData;
    
    public Image() {
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
	 * @return the imageId
	 */
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * Compares two Image objects for similarity
	 *
	 * @param obj Image object to compare to
	 * @return boolean true/false whether or not they are the same objects
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @should equal Image with same imageId
	 * @should not equal Image with different imageId
	 * @should not equal on null
	 * @should have equal image objects with no imageIds
	 * @should not have equal Image objects when one has null imageId
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Image) {
			Image images = (Image) obj;
			if (this.getId() != null && images.getId() != null) {
				return (this.getId().equals(images.getId()));
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
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
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
     * @return the provider
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * @return the providerId
     */
    public Integer getProviderId() {
        return providerId;
    }

    /**
     * @param providerId the providerId to set
     */
    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the locationId
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * @param locationId the locationId to set
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * @return the imageData
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * @param imageData the imageData to set
     */
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
