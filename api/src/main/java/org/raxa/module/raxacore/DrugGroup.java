/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore;

import java.io.Serializable;
import java.util.Date;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;

/**
 *
 * @author Yan
 */
public class DrugGroup extends BaseOpenmrsMetadata implements Serializable {
	
	public static final long serialVersionUID = 285L;
	
	private Integer id;
	
	private String drugGroupName;
	
	private Date dateCreated;
	
	private String uuid;
	
	public DrugGroup() {
	}
	
	/**
	 * @return the id
	 */
	
	@Override
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DrugGroup) {
			DrugGroup dg = (DrugGroup) obj;
			if (this.getId() != null && dg.getId() != null)
				return (this.getId().equals(dg.getId()));
		}
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		if (this.getId() == null)
			return super.hashCode();
		return this.getId().hashCode();
	}
	
	/**
	 * @return the drugGroupName
	 */
	public String getDrugGroupName() {
		return drugGroupName;
	}
	
	/**
	 * @param drugGroupName the drugGroupName to set
	 */
	public void setDrugGroupName(String drugGroupName) {
		this.drugGroupName = drugGroupName;
	}
	
	/**
	 * @return the dateCreated
	 */
	@Override
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @param dateCreated the dateCreated to set
	 */
	@Override
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * @return the uuid
	 */
	@Override
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
