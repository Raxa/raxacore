package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.List;

public class PatientMapper {
	
	private PatientService patientService;
	
	private PersonNameMapper personNameMapper;
	
	private BirthDateMapper birthDateMapper;
	
	private PersonAttributeMapper personAttributeMapper;
	
	private AddressMapper addressMapper;
	
	public PatientMapper(PersonNameMapper personNameMapper, BirthDateMapper birthDateMapper,
	    PersonAttributeMapper personAttributeMapper, AddressMapper addressMapper) {
		this.personNameMapper = personNameMapper;
		this.birthDateMapper = birthDateMapper;
		this.personAttributeMapper = personAttributeMapper;
		this.addressMapper = addressMapper;
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
		createIdentifier(bahmniPatient, patient);
		return patient;
	}
	
	private void createIdentifier(BahmniPatient bahmniPatient, Patient patient) {
		PatientIdentifier patientIdentifier;
		String existingIdentifierValue = bahmniPatient.getPatientIdentifier();
		
		if (existingIdentifierValue == null || existingIdentifierValue.trim().isEmpty()) {
			patientIdentifier = generateIdentifier(bahmniPatient.getCenterName());
		} else {
			PatientService ps = getPatientService();
			PatientIdentifierType jss = ps.getPatientIdentifierTypeByName("JSS");
			patientIdentifier = new PatientIdentifier(existingIdentifierValue, jss, null);
		}
		
		patientIdentifier.setPreferred(true);
		patient.addIdentifier(patientIdentifier);
	}
	
	public PatientService getPatientService() {
		if (patientService == null)
			patientService = Context.getPatientService();
		return patientService;
	}
	
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
	
	private PatientIdentifier generateIdentifier(String centerName) {
		IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
		List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);
		String center = centerName;
		for (IdentifierSource identifierSource : allIdentifierSources) {
			if (identifierSource.getName().equals(center)) {
				String identifier = identifierSourceService.generateIdentifier(identifierSource, "Bahmni Registration App");
				PatientIdentifierType identifierType = identifierSource.getIdentifierType();
				return new PatientIdentifier(identifier, identifierType, null);
			}
		}
		return null;
	}
}
