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
        String closestMatchVillage = lavensteinsDistance.getClosestMatch(scrubData(personAddress.getVillage()), hierarchy.getAllVillages());
        List<SanitizerPersonAddress> addresses = hierarchy.getAllAddressWithVillageName(closestMatchVillage);

        if (addresses.size() > 1) {
            return lavensteinsDistance.getClosestMatch(scrubData(personAddress.getTehsil()), addresses, AddressField.TEHSIL);
        }
        return addresses.get(0);
    }

    public String scrubData(String value) {
        if(value == null)
            return null;
        return value.replace("\\", "").trim();
    }

}
