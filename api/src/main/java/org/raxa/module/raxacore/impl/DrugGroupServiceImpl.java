package org.raxa.module.raxacore.impl;

import java.util.List;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;
import org.raxa.module.raxacore.db.DrugGroupDAO;
import org.springframework.transaction.annotation.Transactional;

public class DrugGroupServiceImpl extends BaseOpenmrsService implements DrugGroupService {

    private DrugGroupDAO dao;

    public void setDrugGroupDAO(DrugGroupDAO dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    @Override
    public DrugGroup getDrugGroup(Integer id) {
        return dao.getDrugGroup(id);
    }

    @Transactional(readOnly = true)
    public DrugGroup getDrugGroupByUuid(String uuid) {
        return dao.getDrugGroupByUuid(uuid);
    }

    @Transactional(readOnly = true)
    public List<DrugGroup> getDrugGroupList() {
        return dao.getDrugGroupList();
    }

    @Transactional
    public DrugGroup saveDrugGroup(DrugGroup drugGroup) {
        return dao.saveDrugGroup(drugGroup);
    }
}
