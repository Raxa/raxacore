package org.raxa.module.raxacore.db;

import java.util.List;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugGroup;

public interface DrugGroupDAO {
	
	public DrugGroup getDrugGroup(Integer id);
	
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	public List<DrugGroup> getDrugGroupByName(String name) throws DAOException;
	
	public List<DrugGroup> getDrugGroupList();
	
	public DrugGroup saveDrugGroup(DrugGroup drugGroup);
	
	public void deleteDrugGroup(DrugGroup drugGroup) throws DAOException;
	
	DrugGroup updateDrugGroup(DrugGroup drugGroup) throws DAOException;
}
