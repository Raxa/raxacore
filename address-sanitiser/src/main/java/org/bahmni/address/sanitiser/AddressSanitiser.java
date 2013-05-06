package org.bahmni.address.sanitiser;

public class AddressSanitiser {

    private final LavensteinsDistance lavensteinsDistance;
    private final AddressHierarchy hierarchy;

    public AddressSanitiser(LavensteinsDistance lavensteinsDistance, AddressHierarchy hierarchy) {
        this.lavensteinsDistance = lavensteinsDistance;
        this.hierarchy = hierarchy;
    }

    public PersonAddress sanitise(PersonAddress personAddress){
        String closestMatchVillage = lavensteinsDistance.getClosestMatch(personAddress.getVillage());
        PersonAddress address = hierarchy.getAddressHierarchyFor(closestMatchVillage);
        return address;
    }
}
