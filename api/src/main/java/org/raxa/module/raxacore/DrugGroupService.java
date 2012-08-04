package org.raxa.module.raxacore;

import java.util.List;
import org.openmrs.api.OpenmrsService;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugGroupDAO;
import org.springframework.transaction.annotation.Transactional;

public interface DrugGroupService extends OpenmrsService {
	
	public DrugGroup saveDrugGroup(DrugGroup drugGroup);
	
	public DrugGroup getDrugGroup(Integer id);
	
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	public List<DrugGroup> getDrugGroupByName(String name);
	
	public List<DrugGroup> getDrugGroupList();
	
	@Authorized( { "Edit Drug Groups" })
	DrugGroup updateDrugGroup(DrugGroup drugGroup);
	
	@Authorized( { "Delete Drug Groups" })
	public void deleteDrugGroup(DrugGroup drugGroup);
}
