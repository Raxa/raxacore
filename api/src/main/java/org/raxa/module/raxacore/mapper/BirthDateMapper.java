package org.raxa.module.raxacore.mapper;

import org.openmrs.Patient;
import org.raxa.module.raxacore.model.BahmniPatient;

import java.util.Date;

public class BirthDateMapper {
	
	public Patient map(Patient patient, BahmniPatient bahmniPatient) {
		Date birthdate = bahmniPatient.getBirthdate();
		Integer age = bahmniPatient.getAge();
		if (birthdate != null) {
			patient.setBirthdate(birthdate);
			patient.setBirthdateEstimated(Boolean.FALSE);
			
		} else if (age != null) {
			patient.setBirthdateFromAge(age, new Date());
			patient.setBirthdateEstimated(Boolean.TRUE);
		}
		return patient;
	}
	
}
