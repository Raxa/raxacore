package org.openmrs.module.bahmniemrapi.visitlocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.builder.LocationBuilder;
import org.openmrs.module.bahmniemrapi.builder.VisitBuilder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
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

        bahmniVisitLocationService = new BahmniVisitLocationServiceImpl(locationService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfGetLocationByUuidReturnsNull() {
        when(locationService.getLocationByUuid("someLocationUuid")).thenReturn(null);

        bahmniVisitLocationService.getVisitLocationUuid("someLocationUuid");
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
    public void shouldGetNullIfMatchingVisitNotFound() {
        Location location1 = new Location();
        location1.setUuid("locationUuid1");

        Location location2 = new Location();
        location2.setUuid("locationUuid2");

        Location location3 = new Location();
        location3.setUuid("locationUuid3");
        location3.addTag(new LocationTag("Visit Location", "Visit Location"));

        Visit visit1 = new VisitBuilder().withUUID("visitUuid1").withLocation(location1).build();
        Visit visit2 = new VisitBuilder().withUUID("visitUuid2").withLocation(location2).build();

        when(locationService.getLocationByUuid("locationUuid3")).thenReturn(location3);

        Visit matchingVisit = bahmniVisitLocationService.getMatchingVisitInLocation(Arrays.asList(visit1, visit2), "locationUuid3");
        Assert.assertNull(matchingVisit);
    }

    @Test
    public void shouldRetrievePassedInLocationIfItIsTaggedAsVisitLocation() {
        Location location = visitLocation();

        when(locationService.getLocationByUuid(location.getUuid())).thenReturn(location);

        Location visitLocation = bahmniVisitLocationService.getVisitLocation(location.getUuid());

        assertEquals(visitLocation, location);
    }

    @Test
    public void shouldRetrieveParentTaggedWithVisitLocationIfPassedInLocationIsNotTagged() {
        Location location = new LocationBuilder().withParent(visitLocation()).build();

        when(locationService.getLocationByUuid(location.getUuid())).thenReturn(location);

        Location actualVisitLocation = bahmniVisitLocationService.getVisitLocation(location.getUuid());

        assertEquals(location.getParentLocation(), actualVisitLocation);
    }

    private Location visitLocation() {
        return new LocationBuilder().withVisitLocationTag().build();
    }

    @Test(expected = VisitLocationNotFoundException.class)
    public void shouldThrowErrorIfNoVisitLocationAvailableInHierarchy() {
        Location location = new Location();

        when(locationService.getLocationByUuid(location.getUuid())).thenReturn(location);

        bahmniVisitLocationService.getVisitLocation(location.getUuid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfLocationNotFound() {
        when(locationService.getLocationByUuid(any(String.class))).thenReturn(null);

        bahmniVisitLocationService.getVisitLocation("non-existent location uuid");
    }
}
