/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore;

import java.util.List;
import org.openmrs.api.OpenmrsService;

/**
 *
 * @author Yan
 */
public interface DrugGroupService extends OpenmrsService {
	
	public DrugGroup saveDrugGroup(DrugGroup drugGroup);
	
	public DrugGroup getDrugGroup(Integer id);
	
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	public List<DrugGroup> getDrugGroupList();
	
}
