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
        when(addressHierarchy.getAllVillages()).thenReturn(Arrays.asList("Badwahi", "Badwar"));
        lavensteinsDistance = new LavensteinsDistance(addressHierarchy);
    }

    @Test
    public void shouldGetClosestMatch() {
        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("baaaandwahi"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("baaandhwar"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("band war"));
        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("band wahri"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("bandwarh"));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("badwara"));
    }
}
