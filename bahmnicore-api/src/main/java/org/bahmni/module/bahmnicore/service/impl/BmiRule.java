package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.Rule;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BMIRule implements Rule {

    private final String WEIGHT_NAME = "Weight";
    private final String HEIGHT_NAME = "Height";

    @Override
    public Double getDose(String patientUuid, Double baseDose) {
        Person person = Context.getPatientService().getPatientByUuid(patientUuid).getPerson();
        Double height = getHeight(person);
        Double weight = getWeight(person);
        Double bsa = calculateBMI(height, weight);

        return bsa*baseDose;
    }

    private static Double calculateBMI(Double height, Double weight) {
        Double heightInMeters = height / 100;
        Double value = weight / (heightInMeters * heightInMeters);
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Double getWeight(Person person) {
        //use CEIL uuid to fetch weight
        ObsService obsService = Context.getObsService();
        Concept weight = Context.getConceptService().getConcept(WEIGHT_NAME);
        List<Obs> obss = obsService.getObservationsByPersonAndConcept(person, weight);
        return obss.size()>0 ? obss.get(0).getValueNumeric() : null;
    }

    private Double getHeight(Person person) {
        //use CEIL uuid to fetch height
        ObsService obsService = Context.getObsService();
        Concept height = Context.getConceptService().getConcept(HEIGHT_NAME);
        List<Obs> obss = obsService.getObservationsByPersonAndConcept(person, height);
        return obss.size()>0 ? obss.get(0).getValueNumeric() : null;
    }
}
