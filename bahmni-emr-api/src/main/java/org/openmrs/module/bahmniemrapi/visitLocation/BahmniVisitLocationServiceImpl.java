package org.openmrs.module.bahmniemrapi.visitlocation;


import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class BahmniVisitLocationServiceImpl implements BahmniVisitLocationService {

    public static final String VISIT_LOCATION = "Visit Location";

    @Override
    public String getVisitLocationForLoginLocation(String loginLocationUuid) {
        Location location = Context.getLocationService().getLocationByUuid(loginLocationUuid);
        while (location != null) {
            if (location.hasTag(VISIT_LOCATION)) {
                return location.getUuid();
            }
            if(location.getParentLocation() == null) return location.getUuid();
            location = location.getParentLocation();
        }
        return null;
    }

    @Override
    public Location getVisitLocationForLoginLocation1(String loginLocationUuid) {
        String visitLocationForLoginLocation = getVisitLocationForLoginLocation(loginLocationUuid);
        return Context.getLocationService().getLocationByUuid(visitLocationForLoginLocation);
    }

    @Override
    public Visit getMatchingVisitInLocation(List<Visit> visits, String locationUuid) {
        String visitLocation = getVisitLocationForLoginLocation(locationUuid);
        Visit visitWithoutLocation = null;
        for(Visit visit : visits) {
            if(visit.getLocation() == null) {
                visitWithoutLocation = visit;
            }
            else if(visit.getLocation().getUuid().equals(visitLocation)){
                return visit;
            }
        }
        if(visitWithoutLocation != null) {
            return visitWithoutLocation;
        }
        return null;
    }

}
