package org.raxa.module.raxacore.mapper;

import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.raxa.module.raxacore.model.BahmniName;

import java.util.List;

public class PersonNameMapper {
	
	public Patient map(Patient patient, List<BahmniName> names) {
		for (BahmniName name : names) {
			patient.addName(new PersonName(name.getGivenName(), name.getMiddleName(), name.getFamilyName()));
		}
		return patient;
	}
}
