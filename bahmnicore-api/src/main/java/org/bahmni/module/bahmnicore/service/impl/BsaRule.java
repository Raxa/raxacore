package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.Rule;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BSARule implements Rule {

    private final String REGISTRATION_ENCOUNTER_TYPE = "Registration";
    private final String WEIGHT_NAME = "Weight";
    private final String HEIGHT_NAME = "Height";

    @Override
    public Double getDose(String patientUuid, Double baseDose) {

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        Encounter selectedEncounter = getLatestEncounterByPatient(patient);

        Integer ageInYears = ageInYears(patient, selectedEncounter.getEncounterDatetime());

        Double height = getHeight(patient, selectedEncounter);
        Double weight = getWeight(patient, selectedEncounter);
        Double bsa = calculateBSA(height, weight, ageInYears);

        return bsa*baseDose;
    }

    private Encounter getLatestEncounterByPatient(Patient patient) {
        EncounterType registration = Context.getEncounterService().getEncounterType(REGISTRATION_ENCOUNTER_TYPE);
        List<Encounter> encounters = Context.getEncounterService()
            .getEncounters(patient, null, null, null, null, Arrays.asList(registration), null, null, null, false);
        if(0 == encounters.size()) return null;

        Encounter selectedEncounter = encounters.get(0);
        if(null==selectedEncounter) return null;

        for (Encounter encounter : encounters) {
            if(encounter.getEncounterDatetime().after(selectedEncounter.getEncounterDatetime())){
                selectedEncounter = encounter;
            }
        }
        return selectedEncounter;
    }

    private static Integer ageInYears(Patient patient, Date asOfDate) {
        Date birthdate = patient.getBirthdate();
        return Years.yearsBetween(new LocalDate(birthdate), new LocalDate(asOfDate)).getYears();
    }

    private static Double calculateBSA(Double height, Double weight, Integer patientAgeInYears) {
        if (patientAgeInYears <= 15 && weight <= 40) {
            return Math.sqrt(weight * height / 3600);
        }
        return Math.pow(weight, 0.425) * Math.pow(height, 0.725) * 0.007184;
    }

    private Double getWeight(Person person, Encounter selectedEncounter) {
        //use CEIL uuid to fetch weight
        ObsService obsService = Context.getObsService();
        Concept weight = Context.getConceptService().getConcept(WEIGHT_NAME);

        List<Obs> obss = obsService.getObservations(Arrays.asList(person),Arrays.asList(selectedEncounter),Arrays.asList(weight),
            null, null, null, null, null, null, null, null, false);

        return obss.size()>0 ? obss.get(0).getValueNumeric() : null;
    }

    private Double getHeight(Person person, Encounter selectedEncounter) {
        //use CEIL uuid to fetch height
        ObsService obsService = Context.getObsService();
        Concept height = Context.getConceptService().getConcept(HEIGHT_NAME);

        List<Obs> obss = obsService.getObservations(Arrays.asList(person),Arrays.asList(selectedEncounter),Arrays.asList(height),
            null, null, null, null, null, null, null, null, false);

        return obss.size()>0 ? obss.get(0).getValueNumeric() : null;
    }

}
