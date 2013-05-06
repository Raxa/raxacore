package org.bahmni.address.sanitiser;

import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;

import java.util.ArrayList;
import java.util.List;

public class AddressHierarchy {
    private AddressHierarchyService addressHierarchyService;

    public AddressHierarchy(AddressHierarchyService addressHierarchyService) {
        this.addressHierarchyService = addressHierarchyService;
    }

    public List<String> getAllVillages() {
        AddressHierarchyLevel villageHierarchyLevel = addressHierarchyService.getBottomAddressHierarchyLevel();
        List<AddressHierarchyEntry> addressHierarchyEntriesByLevel = addressHierarchyService.getAddressHierarchyEntriesByLevel(villageHierarchyLevel);
        List<String> villageList = new ArrayList<String>();
        for (AddressHierarchyEntry addressHierarchyEntry : addressHierarchyEntriesByLevel) {
            villageList.add(addressHierarchyEntry.getLocationName());
        }
        return villageList;
    }

    public PersonAddress getAddressHierarchyFor(String village) {
        AddressHierarchyEntry addressHierarchyEntry = new AddressHierarchyEntry();
        addressHierarchyEntry.setName(village);
        List<String> possibleFullAddresses = addressHierarchyService.getPossibleFullAddresses(addressHierarchyEntry);
        String fullAddress = possibleFullAddresses.get(0);
        String[] split = fullAddress.split("\\|");
        return new PersonAddress(split[3], split[2], split[1], split[0]);
    }
}
