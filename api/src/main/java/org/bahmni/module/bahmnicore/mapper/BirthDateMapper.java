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
			patient.setBirthdateEstimated(false);
			
		} else if (age != null) {
			patient.setBirthdateFromAge(age, new Date());
			patient.setBirthdateEstimated(true);
		}
		return patient;
	}

    public BahmniPatient mapFromPatient(BahmniPatient bahmniPatient, Patient patient) {
        if(bahmniPatient == null){
            bahmniPatient = new BahmniPatient();
        }

        if(patient.getBirthdateEstimated()){
            bahmniPatient.setAge(patient.getAge());
            return bahmniPatient;
        }
        bahmniPatient.setBirthDate(patient.getBirthdate());
        bahmniPatient.setAge(patient.getAge());
        return bahmniPatient;
    }
}
