package org.openmrs.module.bahmniemrapi.visitlocation;

import org.openmrs.Visit;

import java.util.List;

public interface BahmniVisitLocationService {
   String getVisitLocationForLoginLocation(String loginLocationUuid);
   Visit getMatchingVisitInLocation(List<Visit> visits, String locationUuid);
}
