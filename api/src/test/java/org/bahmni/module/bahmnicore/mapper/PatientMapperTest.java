package org.bahmni.module.bahmnicore.mapper;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.model.BahmniName;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.util.PatientMother;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class PatientMapperTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private PatientMapper patientMapper;

    @Test
	public void shouldMapPersonNameToPatient() throws ParseException {
        BahmniPatient bahmniPatient = new PatientMother().buildBahmniPatient();

		Patient patient = patientMapper.map(new Patient(), bahmniPatient);
		
		BahmniName name = bahmniPatient.getNames().get(0);
		assertEquals(name.getGivenName(), patient.getGivenName());
		assertEquals(name.getFamilyName(), patient.getFamilyName());
	}

	@Test
	public void shouldMapDateCreatedForNewPatient() throws ParseException {
        Date dateCreated = new SimpleDateFormat("dd-MM-yyyy").parse("11-03-2013");

        BahmniPatient bahmniPatient = new PatientMother().withDateCreated(dateCreated).buildBahmniPatient();

        Patient patient = patientMapper.map(null, bahmniPatient);

		assertEquals(dateCreated, patient.getPersonDateCreated());
	}

	@Test
	public void shouldNotMapDateCreatedForExistingPatient() throws ParseException {
        Date dateCreatedBeforeMapping = new SimpleDateFormat("dd-MM-yyyy").parse("11-03-2013");
        BahmniPatient bahmniPatient = new PatientMother().withDateCreated(null).buildBahmniPatient();
        Patient patient = new PatientMother().withDateCreated(dateCreatedBeforeMapping).build();

        Patient mappedPatient = patientMapper.map(patient, bahmniPatient);

		assertEquals(dateCreatedBeforeMapping, mappedPatient.getPersonDateCreated());
	}
}
