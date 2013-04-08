package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniName;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.List;

public class PersonNameMapper {
	
	public Patient map(Patient patient, List<BahmniName> names) {
		for (BahmniName name : names) {
			patient.addName(new PersonName(name.getGivenName(), name.getMiddleName(), name.getFamilyName()));
		}
		return patient;
	}
}
