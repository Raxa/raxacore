package org.bahmni.address.sanitiser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
        when(addressHierarchy.getAddressHierarchyFor(expectedVillage)).thenReturn(new PersonAddress(expectedVillage, tehsil, district, state));

        PersonAddress sanitisedAddress = sanitiser.sanitise(new PersonAddress(village, tehsil, district, state));

        assertEquals(expectedVillage, sanitisedAddress.getVillage());
        assertEquals(tehsil, sanitisedAddress.getTehsil());
        assertEquals(district, sanitisedAddress.getDistrict());
        assertEquals(state, sanitisedAddress.getState());
    }
}
