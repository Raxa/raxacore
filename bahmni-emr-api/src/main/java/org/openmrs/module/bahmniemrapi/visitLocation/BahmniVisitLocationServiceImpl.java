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
    public String getVisitLocationUuid(String loginLocationUuid) {
        Location location = getVisitLocation(loginLocationUuid);
        if (location != null) {
            return location.getUuid();
        }
        return null;
    }

    @Override
    public Location getVisitLocation(String loginLocationUuid) {
        Location location = Context.getLocationService().getLocationByUuid(loginLocationUuid);
        while (location != null) {
            if (location.hasTag(VISIT_LOCATION)) {
                return location;
            }
            if(location.getParentLocation() == null) {
                return location;
            }
            location = location.getParentLocation();
        }
        return null;
    }

    @Override
    public Visit getMatchingVisitInLocation(List<Visit> visits, String locationUuid) {
        String visitLocation = getVisitLocationUuid(locationUuid);
        for(Visit visit : visits) {
            if(visit.getLocation() != null) {
                if(visit.getLocation().getUuid().equals(visitLocation)){
                    return visit;
                }
            }
        }
        return null;
    }

}
