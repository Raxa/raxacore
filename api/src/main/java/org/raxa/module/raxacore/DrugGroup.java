package org.raxa.module.raxacore;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Drug;

public class DrugGroup extends BaseOpenmrsMetadata implements Serializable {
	
	public static final long serialVersionUID = 285L;
	
	private Integer id;
	
	private String drugGroupName;
	
	private Date dateCreated;
	
	private String uuid;
	
	private String searchQuery;
	
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
	
	/**
	 * @return the searchQuery
	 */
	public String getSearchQuery() {
		return searchQuery;
	}
	
	/**
	 * @param searchQuery the searchQuery to set
	 */
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DrugGroup) {
			DrugGroup dg = (DrugGroup) obj;
			if (this.getId() != null && dg.getId() != null) {
				return (this.getId().equals(dg.getId()));
			}
		}
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}
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
