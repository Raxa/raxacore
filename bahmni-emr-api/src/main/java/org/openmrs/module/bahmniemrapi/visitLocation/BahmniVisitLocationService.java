package org.openmrs.module.bahmniemrapi.visitlocation;

import org.openmrs.Location;
import org.openmrs.Visit;

import java.util.List;

public interface BahmniVisitLocationService {
   String getVisitLocationForLoginLocation(String loginLocationUuid);
   Location getVisitLocationForLoginLocation1(String loginLocationUuid);
   Visit getMatchingVisitInLocation(List<Visit> visits, String locationUuid);
}
