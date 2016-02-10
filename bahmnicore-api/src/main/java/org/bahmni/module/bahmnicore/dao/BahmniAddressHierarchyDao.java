package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.model.BahmniAddressHierarchyEntry;

public interface BahmniAddressHierarchyDao {
    BahmniAddressHierarchyEntry getAddressHierarchyEntryByUuid(String uuid);
}
