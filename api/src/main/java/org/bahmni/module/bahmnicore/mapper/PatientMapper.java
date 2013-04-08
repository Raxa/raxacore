package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.*;

public class PatientMapper {
	
	private PersonNameMapper personNameMapper;
	
	private BirthDateMapper birthDateMapper;
	
	private PersonAttributeMapper personAttributeMapper;
	
	private AddressMapper addressMapper;
	
	private final PatientIdentifierMapper patientIdentifierMapper;
	
	private final HealthCenterMapper healthCenterMapper;
	
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
		}
		patient.setGender(bahmniPatient.getGender());
		patient = personNameMapper.map(patient, bahmniPatient.getNames());
		patient = birthDateMapper.map(patient, bahmniPatient);
		patient = personAttributeMapper.map(patient, bahmniPatient.getAttributes());
		patient = addressMapper.addAddresses(patient, bahmniPatient.getAddresses());
		patientIdentifierMapper.createIdentifier(bahmniPatient, patient);
		healthCenterMapper.addHealthCenter(patient, bahmniPatient, this);
		return patient;
	}
}
