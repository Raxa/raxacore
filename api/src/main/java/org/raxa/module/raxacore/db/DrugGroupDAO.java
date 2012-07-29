/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.db;

import java.util.List;
import org.raxa.module.raxacore.DrugGroup;

/**
 *
 * @author Yan
 */
public interface DrugGroupDAO {
	
	DrugGroup getDrugGroup(Integer id);
	
	DrugGroup getDrugGroupByUuid(String uuid);
	
	List<DrugGroup> getDrugGroupList();
	
	DrugGroup saveDrugGroup(DrugGroup drugGroup);
}
