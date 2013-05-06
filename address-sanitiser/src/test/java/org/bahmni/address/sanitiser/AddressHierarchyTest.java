package org.bahmni.address.sanitiser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AddressHierarchyTest {
    @Mock
    AddressHierarchyService addressHierarchyService;

    AddressHierarchy addressHierarchy;

    @Before
    public void setup(){
        initMocks(this);
        addressHierarchy = new AddressHierarchy(addressHierarchyService);
    }

    @Test
    public void shouldGetListOfAllVillages() {
        AddressHierarchyLevel villageHierarchyLevel = new AddressHierarchyLevel();
        when(addressHierarchyService.getBottomAddressHierarchyLevel()).thenReturn(villageHierarchyLevel);
        when(addressHierarchyService.getAddressHierarchyEntriesByLevel(any(AddressHierarchyLevel.class)))
                .thenReturn(Arrays.asList(new AddressHierarchyEntry(), new AddressHierarchyEntry()));

        List<String> allVillages = addressHierarchy.getAllVillages();

        verify(addressHierarchyService).getBottomAddressHierarchyLevel();
        verify(addressHierarchyService).getAddressHierarchyEntriesByLevel(any(AddressHierarchyLevel.class));
        assertEquals(2, allVillages.size());
    }

    @Test
    public void shouldgetAddressHierarchyForaVillage() {
        String village = "Village";
        String state = "State";
        String tehsil = "Tehsil";
        String district = "District";

        when(addressHierarchyService.getBottomAddressHierarchyLevel()).thenReturn(new AddressHierarchyLevel());
        when(addressHierarchyService.getAddressHierarchyEntriesByLevelAndName(any(AddressHierarchyLevel.class), anyString()))
                .thenReturn(Arrays.asList(new AddressHierarchyEntry()));
        when(addressHierarchyService.getPossibleFullAddresses(any(AddressHierarchyEntry.class)))
                .thenReturn(Arrays.asList(state + "|" + district + "|" + tehsil + "|" + village));

        List<PersonAddress> addresses = addressHierarchy.getAddressHierarchyFor(village);

        assertEquals(village, addresses.get(0).getVillage());
        assertEquals(tehsil, addresses.get(0).getTehsil());
        assertEquals(district, addresses.get(0).getDistrict());
        assertEquals(state, addresses.get(0).getState());
    }

    @Test
    public void shouldReturnAllPossibleAddressValuesForAVillage() {
        String village = "Village";
        String state = "State";
        String tehsil = "Tehsil";
        String district = "District";
        AddressHierarchyEntry addressHierarchyEntry1 = new AddressHierarchyEntry();
        AddressHierarchyEntry addressHierarchyEntry2 = new AddressHierarchyEntry();

        when(addressHierarchyService.getBottomAddressHierarchyLevel()).thenReturn(new AddressHierarchyLevel());
        when(addressHierarchyService.getAddressHierarchyEntriesByLevelAndName(any(AddressHierarchyLevel.class), anyString()))
                .thenReturn(Arrays.asList(addressHierarchyEntry1, addressHierarchyEntry2));
        when(addressHierarchyService.getPossibleFullAddresses(any(AddressHierarchyEntry.class)))
                .thenReturn(Arrays.asList(state + "|" + district + "|" + tehsil + "|" + village));

        List<PersonAddress> addresses = addressHierarchy.getAddressHierarchyFor(village);

        assertEquals(2, addresses.size());
    }
}
