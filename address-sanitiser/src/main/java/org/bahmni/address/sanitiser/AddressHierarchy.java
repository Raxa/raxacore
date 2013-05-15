package org.bahmni.address.sanitiser;

import org.bahmni.address.AddressHierarchyEntry;
import org.bahmni.address.AddressQueryExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressHierarchy {
    public static List<String> villageList;
    private AddressQueryExecutor addressQueryExecutor;

    public AddressHierarchy(AddressQueryExecutor addressQueryExecutor) {
        this.addressQueryExecutor = addressQueryExecutor;
    }

    public List<String> getAllVillages() {
        if(villageList != null)
            return villageList;
        villageList = addressQueryExecutor.getAllVillages();
        return villageList;
    }

    public List<SanitizerPersonAddress> getAllAddressWithVillageName(String village) {
        List<Integer> tehsilIds = addressQueryExecutor.findTehsilIdsFor(village);
        List<SanitizerPersonAddress> sanitizerPersonAddresses = new ArrayList<SanitizerPersonAddress>();
        for (int tehsilId : tehsilIds) {
            AddressHierarchyEntry tehsilEntry = addressQueryExecutor.findHigherLevelsHierarchyEntry(tehsilId, 1);
            AddressHierarchyEntry districtEntry = addressQueryExecutor.findHigherLevelsHierarchyEntry(tehsilEntry.getParentId(), 2);
            AddressHierarchyEntry stateEntry = addressQueryExecutor.findHigherLevelsHierarchyEntry(districtEntry.getParentId(), 3);

            sanitizerPersonAddresses.add(new SanitizerPersonAddress(village, tehsilEntry.getName(), districtEntry.getName(), stateEntry.getName()));
        }
        return sanitizerPersonAddresses;
    }
}
