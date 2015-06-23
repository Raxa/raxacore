package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
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
    private EncounterTypeIdentifier encounterTypeIdentifier;

    @Autowired
    public EncounterSessionMatcher(@Qualifier("adminService") AdministrationService administrationService, EncounterTypeIdentifier encounterTypeIdentifier) {
        this.adminService = administrationService;
        this.encounterTypeIdentifier = encounterTypeIdentifier;
    }


    @Override
    public Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {
        Provider provider = null;
        if (encounterParameters.getProviders() != null && !encounterParameters.getProviders().isEmpty())
            provider = encounterParameters.getProviders().iterator().next();

        if (BahmniEncounterTransaction.isRetrospectiveEntry(encounterParameters.getEncounterDateTime())) {
            return findRetrospectiveEncounter(visit, encounterParameters, provider);
        } else {
            return findRegularEncounter(visit, encounterParameters, provider);
        }
    }

    private Encounter findRegularEncounter(Visit visit, EncounterParameters encounterParameters, Provider provider) {
        EncounterType encounterType = getEncounterType(encounterParameters);

        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (!encounter.isVoided() && encounterType.equals(encounter.getEncounterType())) {
                    Date encounterDateChanged = encounter.getDateChanged() == null ? encounter.getDateCreated() : encounter.getDateChanged();
                    if (!isCurrentSessionTimeExpired(encounterDateChanged) && isSameProvider(provider, encounter) && areSameEncounterDates(encounter, encounterParameters))
                        if (isLocationNotDefined(encounterParameters, encounter) || isSameLocation(encounterParameters, encounter))
                            return encounter;
                }
            }
        }
        return null;
    }

    private EncounterType getEncounterType(EncounterParameters encounterParameters) {
        EncounterType encounterType = encounterParameters.getEncounterType();

        if (encounterType == null) {
            Location location = encounterParameters.getLocation();
            String locationUuid = null;
            if(location != null){
                locationUuid = location.getUuid();
            }
            encounterType = encounterTypeIdentifier.getEncounterTypeFor(locationUuid);
        }
        return encounterType;
    }

    private Encounter findRetrospectiveEncounter(Visit visit, EncounterParameters encounterParameters, Provider provider) {
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (!encounter.isVoided() && (encounterParameters.getEncounterType() == null || encounterParameters.getEncounterType().equals(encounter.getEncounterType()))) {
                    if (isSameProvider(provider, encounter) && areSameEncounterDates(encounter, encounterParameters)) {
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

    private boolean isLocationNotDefined(EncounterParameters encounterParameters, Encounter encounter) {
        return (encounterParameters.getLocation() == null && encounter.getLocation() == null);
    }

    private boolean isSameProvider(Provider provider, Encounter encounter) {
        if (provider == null || CollectionUtils.isEmpty(encounter.getEncounterProviders())
                || (encounter.getCreator().getId() != Context.getUserContext().getAuthenticatedUser().getId())
                ) {
            return false;
        }
        for (EncounterProvider encounterProvider : encounter.getEncounterProviders()) {
            if (encounterProvider.getProvider().getProviderId().equals(provider.getId()))
                return true;
        }
        return false;
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