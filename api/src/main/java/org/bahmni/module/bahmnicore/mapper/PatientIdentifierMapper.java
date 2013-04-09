package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.List;

public class PatientIdentifierMapper {
	
	private PatientService patientService;
	
	public Patient map(BahmniPatient bahmniPatient, Patient patient) {
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
        return patient;
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
