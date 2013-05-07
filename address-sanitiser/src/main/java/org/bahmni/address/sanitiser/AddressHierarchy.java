package org.bahmni.address.sanitiser;

import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressHierarchy {

    private AddressHierarchyService addressHierarchyService;
    public static List<String> villageList;

    public AddressHierarchy() {
    }

    public AddressHierarchy(AddressHierarchyService addressHierarchyService) {
        this.addressHierarchyService = addressHierarchyService;
    }

    private AddressHierarchyService getAddressHierarchyService() {
        return addressHierarchyService != null ? addressHierarchyService : Context.getService(AddressHierarchyService.class);
    }

    public List<String> getAllVillages() {
        if(villageList != null)
            return villageList;
        AddressHierarchyService service = getAddressHierarchyService();
        AddressHierarchyLevel villageHierarchyLevel = service.getBottomAddressHierarchyLevel();
        List<AddressHierarchyEntry> addressHierarchyEntriesByLevel = service.getAddressHierarchyEntriesByLevel(villageHierarchyLevel);
        villageList = new ArrayList<String>();
        for (AddressHierarchyEntry addressHierarchyEntry : addressHierarchyEntriesByLevel) {
            villageList.add(addressHierarchyEntry.getLocationName());
        }

        return villageList;
    }

    public List<PersonAddress> getAddressHierarchyFor(String village) {
        AddressHierarchyService service = getAddressHierarchyService();
        AddressHierarchyLevel villageHierarchyLevel = service.getBottomAddressHierarchyLevel();
        List<AddressHierarchyEntry> addressHierarchyEntries =
                service.getAddressHierarchyEntriesByLevelAndName(villageHierarchyLevel, village);

        List<String> possibleFullAddresses = new ArrayList<String>();
        for (AddressHierarchyEntry addressHierarchyEntry : addressHierarchyEntries) {
            possibleFullAddresses.addAll(service.getPossibleFullAddresses(addressHierarchyEntry));
        }

        List<PersonAddress> possibleAddresses = new ArrayList<PersonAddress>();
        for (String possibleFullAddress : possibleFullAddresses) {
            String[] split = possibleFullAddress.split("\\|");
            possibleAddresses.add(new PersonAddress(split[3], split[2], split[1], split[0]));
        }
        return possibleAddresses;
    }
}
