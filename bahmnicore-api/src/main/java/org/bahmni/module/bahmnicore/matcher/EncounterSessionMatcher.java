package org.bahmni.module.bahmnicore.matcher;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;


public class EncounterSessionMatcher implements BaseEncounterMatcher {

    public static final int DEFAULT_SESSION_DURATION = 60;
    private AdministrationService adminService;

    public EncounterSessionMatcher(AdministrationService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {
        EncounterType encounterType = encounterParameters.getEncounterType();
        Provider provider = encounterParameters.getProviders().iterator().next();

        if (encounterType == null){
            throw new IllegalArgumentException("Encounter Type not found");
        }

        if(visit.getEncounters()!=null){
            for (Encounter encounter : visit.getEncounters()) {
                if (encounterType.equals(encounter.getEncounterType())) {
                    Interval interval = new Interval(new DateTime(encounter.getEncounterDatetime()), DateTime.now());
                    if(!isCurrentSessionTimeExpired(interval) && isSameProvider(provider, encounter))
                        return encounter;
                }
            }
        }
        return null;
    }

    private boolean isSameProvider(Provider provider, Encounter encounter) {
        return provider != null && encounter.getProvider().getId().equals(provider.getId());
    }

    private boolean isCurrentSessionTimeExpired(Interval interval) {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int sessionDuration = DEFAULT_SESSION_DURATION;
        if(configuredSessionDuration != null)
            sessionDuration = Integer.parseInt(configuredSessionDuration);

        Period period = interval.toDuration().toPeriod();
        return (period.getHours() * 60 + period.getMinutes()) > sessionDuration;
    }
}
