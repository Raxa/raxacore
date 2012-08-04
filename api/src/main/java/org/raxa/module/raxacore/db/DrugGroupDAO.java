package org.raxa.module.raxacore.db;

import java.util.List;
import org.raxa.module.raxacore.DrugGroup;

public interface DrugGroupDAO {
	
	DrugGroup getDrugGroup(Integer id);
	
	DrugGroup getDrugGroupByUuid(String uuid);
	
	List<DrugGroup> getDrugGroupList();
	
	DrugGroup saveDrugGroup(DrugGroup drugGroup);
}
