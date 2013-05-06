package org.bahmni.address.sanitiser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressSanitiser {

    private final LavensteinsDistance lavensteinsDistance;
    private final AddressHierarchy hierarchy;

    @Autowired
    public AddressSanitiser(LavensteinsDistance lavensteinsDistance, AddressHierarchy hierarchy) {
        this.lavensteinsDistance = lavensteinsDistance;
        this.hierarchy = hierarchy;
    }

    public PersonAddress sanitise(PersonAddress personAddress){
        String closestMatchVillage = lavensteinsDistance.getClosestMatch(personAddress.getVillage(), hierarchy.getAllVillages());
        List<PersonAddress> addresses = hierarchy.getAddressHierarchyFor(closestMatchVillage);

        if(addresses.size() > 1){
            return lavensteinsDistance.getClosestMatch(personAddress.getTehsil(),addresses,AddressField.TEHSIL);
        }
        return addresses.get(0);
    }
}
