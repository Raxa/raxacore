package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniName;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.List;

public class PersonNameMapper {
	
	public Patient map(Patient patient, List<BahmniName> names) {
		
		int oldNumberOfNames = patient.getNames().size();
		addName(patient, names);
		int newNumberOfNames = patient.getNames().size();
		
		voidEarlierNames(patient, oldNumberOfNames, newNumberOfNames);
		
		return patient;
	}
	
	private void voidEarlierNames(Patient patient, int oldNumberOfNames, int newNumberOfNames) {
		if (newNumberOfNames > oldNumberOfNames) {
			for (PersonName name : patient.getNames()) {
				if (name.getId() != null) {
					name.setVoided(true);
				}
			}
		}
	}
	
	private void addName(Patient patient, List<BahmniName> names) {
		for (BahmniName name : names) {
			patient.addName(new PersonName(name.getGivenName(), name.getMiddleName(), name.getFamilyName()));
		}
	}
}
