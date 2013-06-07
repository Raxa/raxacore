package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    private PersonNameMapper personNameMapper;

    private BirthDateMapper birthDateMapper;

    private PersonAttributeMapper personAttributeMapper;

    private AddressMapper addressMapper;

    private final PatientIdentifierMapper patientIdentifierMapper;

    private final HealthCenterMapper healthCenterMapper;

    @Autowired
    public PatientMapper(PersonNameMapper personNameMapper, BirthDateMapper birthDateMapper,
                         PersonAttributeMapper personAttributeMapper, AddressMapper addressMapper,
                         PatientIdentifierMapper patientIdentifierMapper, HealthCenterMapper healthCenterMapper) {
        this.personNameMapper = personNameMapper;
        this.birthDateMapper = birthDateMapper;
        this.personAttributeMapper = personAttributeMapper;
        this.addressMapper = addressMapper;
        this.patientIdentifierMapper = patientIdentifierMapper;
        this.healthCenterMapper = healthCenterMapper;
    }

    public Patient map(Patient patient, BahmniPatient bahmniPatient) {
        if (patient == null) {
            patient = new Patient();
            patient.setPersonDateCreated(bahmniPatient.getPersonDateCreated());
        }
        patient.setGender(bahmniPatient.getGender());
        patient = personNameMapper.map(patient, bahmniPatient.getNames());
        patient = birthDateMapper.map(patient, bahmniPatient);
        patient = personAttributeMapper.map(patient, bahmniPatient.getAttributes());
        patient = addressMapper.map(patient, bahmniPatient.getAddresses());
        patient = patientIdentifierMapper.map(bahmniPatient, patient);
        patient = healthCenterMapper.map(patient, bahmniPatient);
        return patient;
    }

    public BahmniPatient mapFromPatient(BahmniPatient bahmniPatient, Patient patient) {
        if (bahmniPatient == null) {
            bahmniPatient = new BahmniPatient();
        }
        bahmniPatient.setGender(bahmniPatient.getGender());
        bahmniPatient = personNameMapper.mapFromPatient(bahmniPatient, patient);
        bahmniPatient = personAttributeMapper.mapFromPatient(bahmniPatient, patient);
        bahmniPatient = addressMapper.mapFromPatient(bahmniPatient, patient);
        bahmniPatient = patientIdentifierMapper.mapFromPatient(bahmniPatient, patient);
        bahmniPatient = healthCenterMapper.mapFromPatient(bahmniPatient, patient);
        bahmniPatient = birthDateMapper.mapFromPatient(bahmniPatient, patient);
        return bahmniPatient;
    }
}
