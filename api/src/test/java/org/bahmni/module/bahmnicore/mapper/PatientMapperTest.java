package org.bahmni.module.bahmnicore.mapper;

import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.bahmni.module.bahmnicore.model.BahmniName;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.util.SimpleObjectMother;

import static junit.framework.Assert.assertEquals;

public class PatientMapperTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldMapPersonNameToPatient() {
		BahmniPatient bahmniPerson = new BahmniPatient(SimpleObjectMother.getSimpleObjectWithAllFields());
		PersonAttributeMapper personAttributeMapper = new PersonAttributeMapper();
		PatientMapper patientMapper = new PatientMapper(new PersonNameMapper(), new BirthDateMapper(),
		        personAttributeMapper, new AddressMapper());
		Patient patient = patientMapper.map(new Patient(), bahmniPerson);
		
		BahmniName name = bahmniPerson.getNames().get(0);
		assertEquals(name.getGivenName(), patient.getGivenName());
		assertEquals(name.getMiddleName(), patient.getMiddleName());
		assertEquals(name.getFamilyName(), patient.getFamilyName());
	}
}
