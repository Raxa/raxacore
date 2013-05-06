package org.bahmni.address.sanitiser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LavensteinsDistanceTest {
    @Mock
    AddressHierarchy addressHierarchy;
    LavensteinsDistance lavensteinsDistance;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldGetClosestMatch() {
        when(addressHierarchy.getAllVillages()).thenReturn(Arrays.asList("Badwahi", "Badwar"));
        lavensteinsDistance = new LavensteinsDistance(addressHierarchy);

        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("baaaandwahi"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("baaandhwar"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("band war"));
        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("band wahri"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("bandwarh"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("badwara"));
    }

    @Test
    public void shouldGetClosestMatchingStringFromGivenMasterList() {
        lavensteinsDistance = new LavensteinsDistance(addressHierarchy);
        PersonAddress personAddress1 = new PersonAddress("village", "sun", "district", "state");
        PersonAddress personAddress2 = new PersonAddress("village", "moon", "district", "state");

        PersonAddress closestMatchPersonAddress = lavensteinsDistance.getClosestMatch("son", Arrays.asList(personAddress1, personAddress2), AddressField.TEHSIL);
        assertEquals("sun", closestMatchPersonAddress.getTehsil());
    }
}
