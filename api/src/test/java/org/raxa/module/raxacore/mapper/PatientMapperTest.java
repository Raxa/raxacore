package org.raxa.module.raxacore.mapper;

import org.bahmni.module.bahmnicore.mapper.HealthCenterMapper;
import org.bahmni.module.bahmnicore.mapper.PatientIdentifierMapper;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.model.BahmniName;
import org.raxa.module.raxacore.model.BahmniPatient;
import org.raxa.module.raxacore.util.SimpleObjectMother;

import static junit.framework.Assert.assertEquals;

public class PatientMapperTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldMapPersonNameToPatient() {
		BahmniPatient bahmniPerson = new BahmniPatient(SimpleObjectMother.getSimpleObjectWithAllFields());
		PersonAttributeMapper personAttributeMapper = new PersonAttributeMapper();
		PatientMapper patientMapper = new PatientMapper(new PersonNameMapper(), new BirthDateMapper(),
		        personAttributeMapper, new AddressMapper(), new PatientIdentifierMapper(), new HealthCenterMapper());
		Patient patient = patientMapper.map(new Patient(), bahmniPerson);
		
		BahmniName name = bahmniPerson.getNames().get(0);
		assertEquals(name.getGivenName(), patient.getGivenName());
		assertEquals(name.getMiddleName(), patient.getMiddleName());
		assertEquals(name.getFamilyName(), patient.getFamilyName());
	}
}
