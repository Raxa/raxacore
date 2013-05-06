package org.bahmni.address.sanitiser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AddressSanitiserTest {
    @Mock
    AddressHierarchy addressHierarchy;
    @Mock
    LavensteinsDistance lavensteinsDistance;
    AddressSanitiser sanitiser;

    @Before
    public void setup(){
        initMocks(this);
        sanitiser = new AddressSanitiser(lavensteinsDistance, addressHierarchy);
    }

    @Test
    public void shouldSanitiseAPersonAddress() {
        String village = "village1";
        String expectedVillage = "village";
        String tehsil = "tehsil";
        String district = "district";
        String state = "state";

        when(lavensteinsDistance.getClosestMatch(village)).thenReturn(expectedVillage);
        when(addressHierarchy.getAddressHierarchyFor(expectedVillage)).thenReturn(Arrays.asList(new PersonAddress(expectedVillage, tehsil, district, state)));

        PersonAddress sanitisedAddress = sanitiser.sanitise(new PersonAddress(village, tehsil, district, state));

        assertEquals(expectedVillage, sanitisedAddress.getVillage());
        assertEquals(tehsil, sanitisedAddress.getTehsil());
        assertEquals(district, sanitisedAddress.getDistrict());
        assertEquals(state, sanitisedAddress.getState());
    }
    
    @Test
    public void shouldSanitiseBasedOnTehsilWhenThereAreMultipleEntriesForTheVillage() {
        String village = "village1";
        String expectedVillage = "village";
        String tehsil1 = "tehsil1";
        String tehsil2 = "tehsil2";
        String district = "district";
        String state = "state";
        String tehsil = "tehsil2a";
        PersonAddress personAddress1 = new PersonAddress(expectedVillage, tehsil1, district, state);
        PersonAddress personAddress2 = new PersonAddress(expectedVillage, tehsil2, district, state);
        PersonAddress personAddressToSanitise = new PersonAddress(village, tehsil, district, state);

        when(lavensteinsDistance.getClosestMatch(village)).thenReturn(expectedVillage);
        when(addressHierarchy.getAddressHierarchyFor(expectedVillage))
                .thenReturn(Arrays.asList(personAddress1, personAddress2));
        when(lavensteinsDistance.getClosestMatch(tehsil, Arrays.asList(personAddress1, personAddress2), AddressField.TEHSIL)).thenReturn(personAddress2);

        PersonAddress sanitisedAddress = sanitiser.sanitise(personAddressToSanitise);

        assertEquals(personAddress2.getVillage(), sanitisedAddress.getVillage());
        assertEquals(personAddress2.getTehsil(), sanitisedAddress.getTehsil());
        assertEquals(personAddress2.getDistrict(), sanitisedAddress.getDistrict());
        assertEquals(personAddress2.getState(), sanitisedAddress.getState());

    }
}
