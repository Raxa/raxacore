package org.bahmni.module.admin.csv.service;

import groovy.lang.GroovyClassLoader;
import org.apache.log4j.Logger;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.BahmniPatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.PatientMatchingAlgorithm;
import org.bahmni.module.admin.csv.patientmatchingalgorithm.exception.CannotMatchPatientException;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.Patient;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class PatientMatchService {
    @Autowired
    private BahmniPatientService patientService;

    private static final String PATIENT_MATCHING_ALGORITHM_DIRECTORY = "/patientMatchingAlgorithm/";
    private static final Logger log = Logger.getLogger(PatientMatchService.class);

    public Patient getPatient(String matchingAlgorithmClassName, List<KeyValue> patientAttributes, String patientIdentifier) throws IOException, IllegalAccessException, InstantiationException, CannotMatchPatientException {
        List<Patient> matchingPatients = patientService.get(patientIdentifier);
        return matchPatients(matchingPatients, patientAttributes, matchingAlgorithmClassName);
    }

    private Patient matchPatients(List<Patient> matchingPatients, List<KeyValue> patientAttributes, String matchingAlgorithmClassName) throws IOException, IllegalAccessException, InstantiationException, CannotMatchPatientException {
        if (matchingAlgorithmClassName == null) {
            Patient patient = new BahmniPatientMatchingAlgorithm().run(matchingPatients, patientAttributes);
            return patient;
        }
        Class clazz = new GroovyClassLoader().parseClass(new File(getAlgorithmClassPath(matchingAlgorithmClassName)));
        PatientMatchingAlgorithm patientMatchingAlgorithm = (PatientMatchingAlgorithm) clazz.newInstance();
        log.debug("PatientMatching : Using Algorithm in " + patientMatchingAlgorithm.getClass().getName());
        return patientMatchingAlgorithm.run(matchingPatients, patientAttributes);
    }

    private String getAlgorithmClassPath(String matchingAlgorithmClassName) {
        return OpenmrsUtil.getApplicationDataDirectory() + PATIENT_MATCHING_ALGORITHM_DIRECTORY + matchingAlgorithmClassName;
    }

}
