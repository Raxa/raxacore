package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

@Ignore
public class RaxaPatientControllerTest extends BaseModuleContextSensitiveTest {
	
	@Mock
	private PatientService patientService;
	
	@Test
	public void shouldCallMapToExistingPatient() throws ResponseException {
		RaxaPatientController controller = new RaxaPatientController();
		SimpleObject firstPatientToSave = SimpleObjectMother.getSimpleObjectWithAllFields();
		controller.createNewPatient(firstPatientToSave, null, null);
	}
}
