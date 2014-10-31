package org.bahmni.module.admin.csv.service;

import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class CSVPatientService {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
    private static final String EMR_PRIMARY_IDENTIFIER_TYPE = "emr.primaryIdentifierType";


    @Autowired
    private PatientService patientService;
    @Autowired
    private AdministrationService administrationService;

    public CSVPatientService(PatientService patientService, AdministrationService administrationService) {
        this.patientService = patientService;
        this.administrationService = administrationService;
    }

    public CSVPatientService() {
    }

    public Patient save(PatientRow patientRow) throws ParseException {
        Patient patient = new Patient();
        PersonName personName = new PersonName(patientRow.getFirstName(), patientRow.getMiddleName(), patientRow.getLastName());
        patient.addName(personName);

        if (!StringUtils.isBlank(patientRow.getBirthdate())) {
            patient.setBirthdate(simpleDateFormat.parse(patientRow.getBirthdate()));
        } else if (!StringUtils.isBlank(patientRow.getAge())) {
            patient.setBirthdateFromAge(Integer.parseInt(patientRow.getAge()), new Date());
        }
        patient.setGender(patientRow.getGender());
        patient.addIdentifier(new PatientIdentifier(patientRow.getRegistrationNumber(), getPatientIdentifierType(), null));

        return patientService.savePatient(patient);
    }


    private PatientIdentifierType getPatientIdentifierType() {
        String globalProperty = administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE);
        PatientIdentifierType patientIdentifierByUuid = patientService.getPatientIdentifierTypeByUuid(globalProperty);
        return patientIdentifierByUuid;
    }
}
