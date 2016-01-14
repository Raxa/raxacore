package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.CIELDictionary;
import org.bahmni.module.bahmnicore.service.DoseCalculator;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BSABasedDoseCalculator implements DoseCalculator {

    private final String REGISTRATION_ENCOUNTER_TYPE = "REG";

    @Override
    public Double getDose(String patientUuid, Double baseDose) throws Exception {

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        Encounter selectedEncounter = getLatestEncounterByPatient(patient);

        Integer ageInYears = ageInYears(patient, selectedEncounter.getEncounterDatetime());

        Double height = getHeight(patient, selectedEncounter);
        Double weight = getWeight(patient, selectedEncounter);
        Double bsa = calculateBSA(height, weight, ageInYears);

        return new BigDecimal(bsa*baseDose).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Encounter getLatestEncounterByPatient(Patient patient) {
        EncounterType registration = Context.getEncounterService().getEncounterType(REGISTRATION_ENCOUNTER_TYPE);
        List<Encounter> encounters = Context.getEncounterService()
            .getEncounters(patient, null, null, null, null, Arrays.asList(registration), null, null, null, false);

        Encounter selectedEncounter = encounters.get(0);

        for (Encounter encounter : encounters) {
            if(encounter.getEncounterDatetime().after(selectedEncounter.getEncounterDatetime())){
                selectedEncounter = encounter;
            }
        }
        return selectedEncounter;
    }

    private Integer ageInYears(Patient patient, Date asOfDate) {
        Date birthdate = patient.getBirthdate();
        return Years.yearsBetween(new LocalDate(birthdate), new LocalDate(asOfDate)).getYears();
    }

    private Double calculateBSA(Double height, Double weight, Integer patientAgeInYears) {
        if (patientAgeInYears <= 15 && weight <= 40) {
            return Math.sqrt(weight * height / 3600);
        }
        return Math.pow(weight, 0.425) * Math.pow(height, 0.725) * 0.007184;
    }

    private Double getWeight(Person person, Encounter selectedEncounter) throws Exception {
        ObsService obsService = Context.getObsService();
        Concept weight = Context.getConceptService().getConceptByUuid(CIELDictionary.WEIGHT_UUID);

        List<Obs> obss = obsService.getObservations(Arrays.asList(person), Arrays.asList(selectedEncounter), Arrays.asList(weight),
            null, null, null, null, null, null, null, null, false);
        if(CollectionUtils.isEmpty(obss)){
            throw new Exception("Weight is not available");
        }
        return obss.get(0).getValueNumeric();
    }

    private Double getHeight(Person person, Encounter selectedEncounter) throws Exception {
        ObsService obsService = Context.getObsService();
        Concept height = Context.getConceptService().getConceptByUuid(CIELDictionary.HEIGHT_UUID);

        List<Obs> obss = obsService.getObservations(Arrays.asList(person), Arrays.asList(selectedEncounter), Arrays.asList(height),
            null, null, null, null, null, null, null, null, false);
        if(CollectionUtils.isEmpty(obss)){
            throw new Exception("Height is not available");
        }
        return obss.get(0).getValueNumeric();
    }

}
