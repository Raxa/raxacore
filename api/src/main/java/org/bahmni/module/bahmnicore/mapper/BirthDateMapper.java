package org.bahmni.module.bahmnicore.mapper;

import org.openmrs.Patient;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
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
