package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
        if (encounterParameters.getEncounterUuid() != null) {
            return findEncounterByUuid(visit, encounterParameters.getEncounterUuid());
        }
        if (encounterParameters.getProviders() != null && !encounterParameters.getProviders().isEmpty())
            provider = encounterParameters.getProviders().iterator().next();
        return findRegularEncounter(visit, encounterParameters, provider);
    }


    private Encounter findEncounterByUuid(Visit visit, String encounterUuid) {
        for (Encounter encounter : visit.getEncounters()) {
            if (encounter.getUuid().equals(encounterUuid)) {
                return encounter;
            }
        }
        return null;
    }

    private Encounter findRegularEncounter(Visit visit, EncounterParameters encounterParameters, Provider provider) {
        EncounterType encounterType = getEncounterType(encounterParameters);

        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (!encounter.isVoided() && encounterType.equals(encounter.getEncounterType())) {
                    Date encounterDateChanged = encounter.getDateChanged() == null ? encounter.getDateCreated() : encounter.getDateChanged();
                    if (isCurrentSessionTimeValid(encounterDateChanged, encounterParameters.getEncounterDateTime()) && areSameEncounters(encounter, encounterParameters)){
                        if(null != provider){
                            if(isSameProvider(provider, encounter))
                                return encounter;
                        } else if(isSameUser(encounter)){
                            return encounter;
                        }
                    }
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
            if (location != null) {
                locationUuid = location.getUuid();
            }
            encounterType = encounterTypeIdentifier.getEncounterTypeFor(locationUuid);
        }
        return encounterType;
    }

    private boolean areSameEncounters(Encounter encounter, EncounterParameters encounterParameters) {
        if (encounterParameters.getEncounterUuid() != null) {
            return encounterParameters.getEncounterUuid().equals(encounter.getUuid());
        }
        return encounterParameters.getEncounterDateTime() == null || (DateUtils.isSameDay(encounter.getEncounterDatetime(), encounterParameters.getEncounterDateTime()));
    }

    private boolean isSameUser(Encounter encounter) {
        if (encounter.getCreator().getUuid().equalsIgnoreCase(Context.getAuthenticatedUser().getUuid())) {
            return true;
        }
        return false;
    }


    private boolean isSameProvider(Provider provider, Encounter encounter) {
        if (CollectionUtils.isEmpty(encounter.getEncounterProviders())
                || (encounter.getCreator().getId().intValue() != Context.getUserContext().getAuthenticatedUser().getId().intValue())
                ) {
            return false;
        }
        for (EncounterProvider encounterProvider : encounter.getEncounterProviders()) {
            if (encounterProvider.getProvider().getProviderId().equals(provider.getId()))
                return true;
        }
        return false;
    }

    private boolean isCurrentSessionTimeValid(Date encounterCreatedDate, Date currentEncounterDate) {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int sessionDurationInMinutes = DEFAULT_SESSION_DURATION_IN_MINUTES;
        if (configuredSessionDuration != null) {
            sessionDurationInMinutes = Integer.parseInt(configuredSessionDuration);
        }
        if(null == currentEncounterDate) {
            currentEncounterDate = new Date();
        }
        Date allowedEncounterTime = DateUtils.addMinutes(encounterCreatedDate, sessionDurationInMinutes);
        return currentEncounterDate.after(encounterCreatedDate) && currentEncounterDate.before(allowedEncounterTime);
    }
}