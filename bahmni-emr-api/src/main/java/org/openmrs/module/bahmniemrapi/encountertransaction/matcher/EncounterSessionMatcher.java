package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmnimapping.services.BahmniLocationService;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class EncounterSessionMatcher implements BaseEncounterMatcher {

    public static final int DEFAULT_SESSION_DURATION_IN_MINUTES = 60;
    private AdministrationService adminService;
    private BahmniLocationService bahmniLocationService;

    @Autowired
    public EncounterSessionMatcher(@Qualifier("adminService") AdministrationService administrationService, BahmniLocationService bahmniLocationService) {
        this.adminService = administrationService;
        this.bahmniLocationService = bahmniLocationService;
    }


    @Override
    public Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {
        EncounterType encounterType = encounterParameters.getEncounterType();
        Provider provider = null;
        if (encounterParameters.getProviders() != null && !encounterParameters.getProviders().isEmpty())
            provider = encounterParameters.getProviders().iterator().next();
        if (encounterType == null && encounterParameters.getLocation() != null) {
            encounterType = bahmniLocationService.getEncounterType(encounterParameters.getLocation().getUuid());
        }

        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (!encounter.isVoided() && (encounterType == null || encounterType.equals(encounter.getEncounterType()))) {
                    if (BahmniEncounterTransaction.isRetrospectiveEntry(encounterParameters.getEncounterDateTime())) {
                        if (isSameProvider(provider, encounter) && areSameEncounterDates(encounter, encounterParameters)) {
                            if (locationNotDefined(encounterParameters, encounter) || isSameLocation(encounterParameters, encounter))
                                return encounter;
                        }
                    } else {
                        Date encounterDateChanged = encounter.getDateChanged() == null ? encounter.getDateCreated() : encounter.getDateChanged();
                        if (!isCurrentSessionTimeExpired(encounterDateChanged) && isSameProvider(provider, encounter) && areSameEncounterDates(encounter, encounterParameters))
                            if (locationNotDefined(encounterParameters, encounter) || isSameLocation(encounterParameters, encounter))
                                return encounter;
                    }
                }
            }
        }
        return null;
    }

    private boolean areSameEncounterDates(Encounter encounter, EncounterParameters encounterParameters) {
        return encounterParameters.getEncounterDateTime() == null || (DateUtils.isSameDay(encounter.getEncounterDatetime(), encounterParameters.getEncounterDateTime()));
    }

    private boolean isSameLocation(EncounterParameters encounterParameters, Encounter encounter) {
        return ((encounter.getLocation() != null && encounter.getLocation().equals(encounterParameters.getLocation())));
    }

    private boolean locationNotDefined(EncounterParameters encounterParameters, Encounter encounter) {
        return (encounterParameters.getLocation() == null && encounter.getLocation() == null);
    }

    private boolean isSameProvider(Provider provider, Encounter encounter) {
        if (provider == null || encounter.getProvider() == null) {
            return false;
        }

        return encounter.getProvider().getId().equals(provider.getPerson().getId());
    }

    private boolean isCurrentSessionTimeExpired(Date encounterCreatedDate) {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int sessionDurationInMinutes = DEFAULT_SESSION_DURATION_IN_MINUTES;
        if (configuredSessionDuration != null)
            sessionDurationInMinutes = Integer.parseInt(configuredSessionDuration);
        Date allowedEncounterTime = DateUtils.addMinutes(encounterCreatedDate, sessionDurationInMinutes);

        return DateUtils.truncatedCompareTo(allowedEncounterTime, new Date(), Calendar.MILLISECOND) <= 0;
    }
}