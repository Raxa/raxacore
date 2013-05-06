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
        when(addressHierarchyService.getPossibleFullAddresses(any(AddressHierarchyEntry.class)))
                .thenReturn(Arrays.asList(state + "|" + district + "|" + tehsil + "|"+ village));

        PersonAddress address = addressHierarchy.getAddressHierarchyFor(village);

        assertEquals(village, address.getVillage());
        assertEquals(tehsil, address.getTehsil());
        assertEquals(district, address.getDistrict());
        assertEquals(state, address.getState());
    }
}
