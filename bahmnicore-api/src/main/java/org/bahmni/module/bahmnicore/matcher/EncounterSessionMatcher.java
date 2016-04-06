package org.bahmni.module.bahmnicore.matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class EncounterSessionMatcher implements BaseEncounterMatcher {

    public static final int DEFAULT_SESSION_DURATION_IN_MINUTES = 60;
    public static final String PATIENT_PROGAM_UUID = "patientProgramUuid";
    private AdministrationService adminService;
    private EncounterTypeIdentifier encounterTypeIdentifier;
    private EncounterService encounterService;
    private BahmniProgramWorkflowService bahmniProgramWorkflowService;

    @Autowired
    public EncounterSessionMatcher(@Qualifier("adminService") AdministrationService administrationService,
                                   EncounterTypeIdentifier encounterTypeIdentifier,
                                   EncounterService encounterService,
                                   BahmniProgramWorkflowService bahmniProgramWorkflowService) {
        this.adminService = administrationService;
        this.encounterTypeIdentifier = encounterTypeIdentifier;
        this.encounterService = encounterService;
        this.bahmniProgramWorkflowService = bahmniProgramWorkflowService;
    }


    @Override
    public Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {
        if (encounterParameters.getEncounterUuid() != null) {
            return findEncounterByUuid(visit, encounterParameters.getEncounterUuid());
        }
        return findMatchingEncounter(visit, encounterParameters);
    }

    private Encounter findEncounterByUuid(Visit visit, String encounterUuid) {
        for (Encounter encounter : visit.getEncounters()) {
            if (encounter.getUuid().equals(encounterUuid)) {
                return encounter;
            }
        }
        return null;
    }

    private Encounter findMatchingEncounter(Visit visit, EncounterParameters encounterParameters) {
        Collection<Visit> visits = null;
        List<Encounter> matchingEncounters = new ArrayList<>();
        if(visit != null ) {
           if(visit.getId() == null){ // To handle new Visit scenario where visit will not be persisted in DB and we get a visit obj (Called from emr-api).
               return null;
           }
           visits = Arrays.asList(visit);
        }
        if (null == encounterParameters.getEncounterDateTime()) {
            encounterParameters.setEncounterDateTime(new Date());
        }
        encounterParameters.setEncounterType(getEncounterType(encounterParameters));
        Collection<Encounter> encounters = this.encounterService.getEncounters(encounterParameters.getPatient(), null,
                getSearchStartDate(encounterParameters.getEncounterDateTime()),
                encounterParameters.getEncounterDateTime(), new ArrayList(),
                Arrays.asList(encounterParameters.getEncounterType()),
                encounterParameters.getProviders(), null, visits, false);

        Map<String, Object> context = encounterParameters.getContext();
        if (context != null) {
            encounters = filterByPatientProgram(encounters, (String) context.get(PATIENT_PROGAM_UUID));
        }

        if(CollectionUtils.isNotEmpty(encounters)){
            for (Encounter encounter : encounters) {
                if (CollectionUtils.isNotEmpty(encounterParameters.getProviders())) {
                    matchingEncounters.add(encounter);
                } else if (CollectionUtils.isEmpty(encounter.getEncounterProviders()) && isSameUser(encounter)) {
                     matchingEncounters.add(encounter);
                }
            }
        }
        if(matchingEncounters.size() > 1){
            throw new RuntimeException("More than one encounter matches the criteria");
        }
        if(!matchingEncounters.isEmpty()){
            return matchingEncounters.get(0);
        }
        return null;
    }

    private Collection filterByPatientProgram(Collection<Encounter> encounters, String patientProgramUuid) {
        if (StringUtils.isBlank(patientProgramUuid)) return encounters;

        return CollectionUtils.intersection(encounters,
                bahmniProgramWorkflowService.getEncountersByPatientProgramUuid(patientProgramUuid));
    }

    private Date getSearchStartDate(Date endDate){
        Date startDate = DateUtils.addMinutes(endDate, getSessionDuration() * -1);
        if (!DateUtils.isSameDay(startDate, endDate)){
            return DateUtils.truncate(endDate, Calendar.DATE);
        }
        return startDate;
    }

    private EncounterType getEncounterType(EncounterParameters encounterParameters) {
        EncounterType encounterType = encounterParameters.getEncounterType();
        if (encounterType == null) {
            encounterType = encounterTypeIdentifier.getDefaultEncounterType();
        }
        return encounterType;
    }

    private boolean isSameUser(Encounter encounter) {
        if (encounter.getCreator().getId().intValue() == Context.getUserContext().getAuthenticatedUser().getId().intValue()) {
            return true;
        }
        return false;
    }

    private int getSessionDuration() {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int sessionDurationInMinutes = DEFAULT_SESSION_DURATION_IN_MINUTES;
        if (configuredSessionDuration != null) {
            sessionDurationInMinutes = Integer.parseInt(configuredSessionDuration);
        }
        return sessionDurationInMinutes;
    }
}