package org.bahmni.module.bahmnicore.contract.patient.search;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.visitlocation.BahmniVisitLocationServiceImpl;

public class PatientVisitLocationQueryHelper {

    private Location visitLocation;

    public PatientVisitLocationQueryHelper(String loginLocationUuid) {
        BahmniVisitLocationServiceImpl bahmniVisitLocationService = new BahmniVisitLocationServiceImpl(Context.getLocationService());
        this.visitLocation = bahmniVisitLocationService.getVisitLocation(loginLocationUuid);

    }


    public String appendWhereClause(String where) {
        if(visitLocation == null){
            return where;
        }
        String condition = " v.location_id=" + visitLocation.getLocationId();
        return String.format("%s %s %s", where, "and", condition);

    }

    public String appendVisitJoinClause(String joinClause) {
        if(visitLocation == null){
            return joinClause;
        }
        String condition = "and v.location_id=" + visitLocation.getLocationId();
        return joinClause + condition;
    }
}
