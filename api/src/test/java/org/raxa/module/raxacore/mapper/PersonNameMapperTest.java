package org.raxa.module.raxacore.mapper;

import org.bahmni.module.bahmnicore.mapper.PersonNameMapper;
import org.bahmni.module.bahmnicore.model.BahmniName;
import org.bahmni.module.bahmnicore.util.SimpleObjectMother;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PersonNameMapperTest {
	
	@Test
	public void shouldCreateNewNameIfNoNameExists() {
		Patient patient = new Patient();
		List<BahmniName> names = Arrays.asList(new BahmniName(SimpleObjectMother.getSimpleObjectForName()));
		
		new PersonNameMapper().map(patient, names);
		
		assertEquals(names.get(0).getGivenName(), patient.getGivenName());
		assertEquals(names.get(0).getMiddleName(), patient.getMiddleName());
		assertEquals(names.get(0).getFamilyName(), patient.getFamilyName());
	}
	
	@Test
	public void shouldVoidNamesSavedBeforeIfThereIsAChangeInName() {
		Patient patient = new Patient();
		
		List<BahmniName> names = Arrays.asList(new BahmniName(SimpleObjectMother.getSimpleObjectForName()));
		BahmniName bahmniName = names.get(0);
		PersonName name = new PersonName(bahmniName.getGivenName() + "old", bahmniName.getMiddleName() + "old", bahmniName
		        .getFamilyName());
		name.setId(10);
		patient.addName(name);
		
		new PersonNameMapper().map(patient, names);
		
		Set<PersonName> nameList = patient.getNames();
		PersonName oldName = getByFirstName(bahmniName.getGivenName() + "old", nameList);
		
		assertTrue(oldName.isVoided());
	}
	
	private PersonName getByFirstName(String s, Set<PersonName> nameList) {
		for (PersonName personName : nameList) {
			if (personName.getGivenName().equals(s))
				return personName;
		}
		return null;
	}
}
