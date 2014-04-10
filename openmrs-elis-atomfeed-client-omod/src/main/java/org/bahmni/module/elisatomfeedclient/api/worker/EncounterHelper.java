package org.bahmni.module.elisatomfeedclient.api.worker;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EncounterHelper {
    private static final String ENCOUNTER_TYPE_LAB_RESULT = "LAB_RESULT";
    private EncounterService encounterService;
    private VisitService visitService;
    private VisitHelper visitHelper;
    private EncounterRole unknownEncounterRole = null;



    public EncounterHelper(EncounterService encounterService, VisitService visitService) {
        this.encounterService = encounterService;
        this.visitService = visitService;
        this.visitHelper = new VisitHelper(visitService);
    }

    public List<Encounter> getEncountersForAccession(String accessionUuid, Patient patient, EncounterType encounterType) {
        List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, Arrays.asList(encounterType), null, null, activeVisits, false);
        List<Encounter> matchedEncounters = new ArrayList<>();
        if (encounters != null && !encounters.isEmpty()) {
            for (Encounter encounter : encounters) {
                if (encounterContainsObsPointingToAccession(encounter, accessionUuid)) {
                    matchedEncounters.add(encounter);
                }
            }
        }
        return matchedEncounters;
    }

    private boolean encounterContainsObsPointingToAccession(Encounter encounter, String accessionUuid) {
        Set<Obs> observations = encounter.getObs();
        for (Obs obs : observations) {
            if(obs.getValueText().equals(accessionUuid) &&
                    obs.getConcept().getName().getName().equals(OpenElisAccessionEventWorker.ACCESSION_UUID_CONCEPT)){
                return true;
            }
        }
        return false;
    }

    public Encounter createNewEncounter(EncounterType encounterType, Provider provider, Patient patient) {
        Visit latestActiveVist = visitHelper.getLatestVisit(patient);
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.addProvider(getEncounterRole(),provider);
        encounter.setEncounterType(encounterType);
        encounter.setEncounterDatetime(new Date());
        encounter.setVisit(latestActiveVist);
        return encounter;

    }

    private EncounterRole getEncounterRole() {
        if (unknownEncounterRole == null) {
            for (EncounterRole encounterRole : encounterService.getAllEncounterRoles(false)) {
                if (encounterRole.getName().equalsIgnoreCase("unknown")) {
                    unknownEncounterRole = encounterRole;
                }
            }
        }
        return unknownEncounterRole;
    }

    public boolean hasObservationWithText(String observationText, Encounter encounter) {
        Set<Obs> observations = encounter.getObs();
        if(!observations.isEmpty()){
            for (Obs observation : observations) {
                if(observation.getValueText().equals(observationText)){
                    return true;
                }
            }
        }
        return false;
    }
}
