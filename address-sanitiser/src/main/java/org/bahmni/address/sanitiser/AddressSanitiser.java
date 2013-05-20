package org.bahmni.address.sanitiser;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressSanitiser {

    private final LavensteinsDistance lavensteinsDistance;
    private final AddressHierarchy hierarchy;

    public AddressSanitiser(LavensteinsDistance lavensteinsDistance, AddressHierarchy hierarchy) {
        this.lavensteinsDistance = lavensteinsDistance;
        this.hierarchy = hierarchy;
    }

    public SanitizerPersonAddress sanitise(SanitizerPersonAddress personAddress) {
        String closestMatchVillage = lavensteinsDistance.getClosestMatch(personAddress.getVillage(), hierarchy.getAllVillages());
        List<SanitizerPersonAddress> addresses = hierarchy.getAllAddressWithVillageName(closestMatchVillage);

        if (addresses.size() > 1) {
            return lavensteinsDistance.getClosestMatch(personAddress.getTehsil(), addresses, AddressField.TEHSIL);
        }
        return addresses.get(0);
    }
}
