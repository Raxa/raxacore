package org.openmrs.module.bahmniemrapi.visitlocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.LocationTag;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.builder.VisitBuilder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.openmrs.Location;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class BahmniVisitLocationServiceImplTest {
    private BahmniVisitLocationServiceImpl bahmniVisitLocationService;

    @Mock
    private LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocationService()).thenReturn(locationService);

        bahmniVisitLocationService = new BahmniVisitLocationServiceImpl();
    }

    @Test
    public void shouldReturnNullWhenGetLocationByUuidReturnsNull() {
        when(locationService.getLocationByUuid("someLocationUuid")).thenReturn(null);

        String locationUuid = bahmniVisitLocationService.getVisitLocationUuid("someLocationUuid");
        Assert.assertEquals(null, locationUuid);
    }

    @Test
    public void shouldGetVisitLocationUuidIfTheLocationIsTaggedAsVisitLocation() {
        LocationTag locationTag = new LocationTag("Visit Location", "some description");
        Location location = new Location();
        location.setUuid("loginLocation");
        HashSet<LocationTag> tags = new HashSet<>();
        tags.add(locationTag);
        location.setTags(tags);

        when(locationService.getLocationByUuid("loginLocation")).thenReturn(location);

        String locationUuid = bahmniVisitLocationService.getVisitLocationUuid("loginLocation");
        Assert.assertEquals("loginLocation", locationUuid);
    }

    @Test
    public void shouldGetVisitLocationUuidIfTheLocationIsNotTaggedAsVisitLocationButHasNoParent() {
        Location location = new Location();
        location.setUuid("loginLocation");

        when(locationService.getLocationByUuid("loginLocation")).thenReturn(location);

        String locationUuid = bahmniVisitLocationService.getVisitLocationUuid("loginLocation");
        Assert.assertEquals("loginLocation", locationUuid);
    }

    @Test
    public void shouldGetParentLocationUuidIfParentIsTaggedAsVisitLocationButChildIsNot() {
        Location parentLocation = new Location();
        parentLocation.setUuid("parentLocationUuid");

        HashSet<LocationTag> tags = new HashSet<>();
        LocationTag locationTag = new LocationTag("Visit Location", "some description");
        tags.add(locationTag);
        parentLocation.setTags(tags);

        Location childLocation = new Location();
        childLocation.setParentLocation(parentLocation);
        childLocation.setUuid("childLocationUuid");

        when(locationService.getLocationByUuid("childLocationUuid")).thenReturn(childLocation);

        String locationUuid = bahmniVisitLocationService.getVisitLocationUuid("childLocationUuid");
        Assert.assertEquals("parentLocationUuid", locationUuid);
    }

    @Test
    public void shouldGetMatchingVisitBasedOnLocation() {
        Location location1 = new Location();
        location1.setUuid("locationUuid1");

        Location location2 = new Location();
        location2.setUuid("locationUuid2");

        Visit visit1 = new VisitBuilder().withUUID("visitUuid1").withLocation(location1).build();
        Visit visit2 = new VisitBuilder().withUUID("visitUuid2").withLocation(location2).build();

        when(locationService.getLocationByUuid("locationUuid1")).thenReturn(location1);

        Visit matchingVisit = bahmniVisitLocationService.getMatchingVisitInLocation(Arrays.asList(visit1, visit2), "locationUuid1");
        Assert.assertEquals(visit1, matchingVisit);
    }

    @Test
    public void shouldGetNullIfVisitLocationIsNull() {
        Location otherLocation = new Location();
        otherLocation.setUuid("otherLocation");
        Visit visit1 = new VisitBuilder().withUUID("visitUuid1").withLocation(otherLocation).build();
        when(locationService.getLocationByUuid("locationUuid1")).thenReturn(null);

        Visit matchingVisit = bahmniVisitLocationService.getMatchingVisitInLocation(Arrays.asList(visit1), "locationUuid1");
        Assert.assertNull(matchingVisit);
    }

    @Test
    public void shouldGetNullIfMatchingVisitNotFound() {
        Location location1 = new Location();
        location1.setUuid("locationUuid1");

        Location location2 = new Location();
        location2.setUuid("locationUuid2");

        Location location3 = new Location();
        location3.setUuid("locationUuid3");

        Visit visit1 = new VisitBuilder().withUUID("visitUuid1").withLocation(location1).build();
        Visit visit2 = new VisitBuilder().withUUID("visitUuid2").withLocation(location2).build();

        when(locationService.getLocationByUuid("locationUuid3")).thenReturn(location3);

        Visit matchingVisit = bahmniVisitLocationService.getMatchingVisitInLocation(Arrays.asList(visit1, visit2), "locationUuid3");
        Assert.assertNull(matchingVisit);
    }
}
