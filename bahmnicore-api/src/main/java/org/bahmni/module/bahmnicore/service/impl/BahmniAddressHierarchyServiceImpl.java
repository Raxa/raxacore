package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.BahmniAddressHierarchyDao;
import org.bahmni.module.bahmnicore.model.BahmniAddressHierarchyEntry;
import org.bahmni.module.bahmnicore.service.BahmniAddressHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BahmniAddressHierarchyServiceImpl implements BahmniAddressHierarchyService {
    private BahmniAddressHierarchyDao bahmniAddressHierarchyDao;

    @Autowired
    public BahmniAddressHierarchyServiceImpl(BahmniAddressHierarchyDao bahmniAddressHierarchyDao) {
        this.bahmniAddressHierarchyDao = bahmniAddressHierarchyDao;
    }

    @Override
    public BahmniAddressHierarchyEntry getAddressHierarchyEntryByUuid(String uuid) {
        return bahmniAddressHierarchyDao.getAddressHierarchyEntryByUuid(uuid);
    }
}
