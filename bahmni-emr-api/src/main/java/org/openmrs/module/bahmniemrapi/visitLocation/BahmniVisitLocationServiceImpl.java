package org.openmrs.module.bahmniemrapi.visitLocation;


import org.openmrs.module.bahmniemrapi.visitLocation.BahmniVisitLocationService;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class BahmniVisitLocationServiceImpl implements BahmniVisitLocationService {

    @Override
    public String getVisitLocationForLoginLocation(String loginLocationUuid) {
        Location childLocation = Context.getLocationService().getLocationByUuid(loginLocationUuid);
        LocationTag visitLocationTag = Context.getLocationService().getLocationTagByName("Visit Location");
        List<Location> locationsTaggedToVisit = Context.getLocationService().getLocationsByTag(visitLocationTag);

        while (childLocation != null) {
            Location parentLocation = childLocation.getParentLocation();
            if (parentLocation != null) {
                for (Location taggedLocation : locationsTaggedToVisit) {
                    if (taggedLocation.getUuid().equals(parentLocation.getUuid())) {
                        return parentLocation.getUuid();
                    }
                }
            } else {
                return childLocation.getUuid();
            }
            childLocation = parentLocation;
        }
        return null;
    }
}
