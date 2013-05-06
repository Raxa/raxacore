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

    public List<PersonAddress> getAddressHierarchyFor(String village) {
        List<AddressHierarchyEntry> addressHierarchyEntries =
                addressHierarchyService.getAddressHierarchyEntriesByLevelAndName(addressHierarchyService.getBottomAddressHierarchyLevel(), village);

        List<String> possibleFullAddresses = new ArrayList<String>();
        for (AddressHierarchyEntry addressHierarchyEntry : addressHierarchyEntries) {
            possibleFullAddresses.addAll(addressHierarchyService.getPossibleFullAddresses(addressHierarchyEntry));
        }

        List<PersonAddress> possibleAddresses = new ArrayList<PersonAddress>();
        for (String possibleFullAddress : possibleFullAddresses) {
            String[] split = possibleFullAddress.split("\\|");
            possibleAddresses.add(new PersonAddress(split[3], split[2], split[1], split[0]));
        }
        return possibleAddresses;
    }
}
