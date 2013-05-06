package org.bahmni.address.sanitiser;

import java.util.List;

public class AddressSanitiser {

    private final LavensteinsDistance lavensteinsDistance;
    private final AddressHierarchy hierarchy;

    public AddressSanitiser(LavensteinsDistance lavensteinsDistance, AddressHierarchy hierarchy) {
        this.lavensteinsDistance = lavensteinsDistance;
        this.hierarchy = hierarchy;
    }

    public PersonAddress sanitise(PersonAddress personAddress){
        String closestMatchVillage = lavensteinsDistance.getClosestMatch(personAddress.getVillage());
        List<PersonAddress> addresses = hierarchy.getAddressHierarchyFor(closestMatchVillage);

        if(addresses.size() > 1){
            return lavensteinsDistance.getClosestMatch(personAddress.getTehsil(),addresses,AddressField.TEHSIL);
        }
        return addresses.get(0);
    }
}
