package org.bahmni.address.sanitiser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
        List<String> allVillages = Arrays.asList("Badwahi", "Badwar");
        lavensteinsDistance = new LavensteinsDistance();

        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("baaaandwahi", allVillages));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("baaandhwar", allVillages));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("band war", allVillages));
        assertEquals("Badwahi", lavensteinsDistance.getClosestMatch("band wahri", allVillages));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("bandwarh", allVillages));
        assertEquals("Badwar", lavensteinsDistance.getClosestMatch("badwara", allVillages));
    }

    @Test
    public void shouldGetClosestMatch1() {
        List<String> allVillages = Arrays.asList("AMARKANTAK", "Bilaspur", "Bilaspur");
        lavensteinsDistance = new LavensteinsDistance();

        assertEquals("Bilaspur", lavensteinsDistance.getClosestMatch("Bilaspuri", allVillages));
    }

    @Test
    public void shouldGetClosestMatchingStringFromGivenMasterList() {
        lavensteinsDistance = new LavensteinsDistance();
        PersonAddress personAddress1 = new PersonAddress("village", "sun", "district", "state");
        PersonAddress personAddress2 = new PersonAddress("village", "moon", "district", "state");

        PersonAddress closestMatchPersonAddress = lavensteinsDistance.getClosestMatch("son", Arrays.asList(personAddress1, personAddress2), AddressField.TEHSIL);
        assertEquals("sun", closestMatchPersonAddress.getTehsil());
    }
}
