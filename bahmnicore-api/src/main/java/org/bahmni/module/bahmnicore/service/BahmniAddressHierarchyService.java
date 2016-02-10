package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.BahmniAddressHierarchyEntry;

public interface BahmniAddressHierarchyService {
    BahmniAddressHierarchyEntry getAddressHierarchyEntryByUuid(String uuid);
}
