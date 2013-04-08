package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@Ignore
public class RaxaPatientControllerTest extends BaseModuleContextSensitiveTest {
	
	@Mock
	private PatientService patientService;
	
	@Test
	public void shouldCallMapToExistingPatient() throws Exception {
		RaxaPatientController controller = new RaxaPatientController();
		SimpleObject firstPatientToSave = SimpleObjectMother.getSimpleObjectWithAllFields();
		SimpleObject firstPatientToUpdate = SimpleObjectMother.getSimpleObjectWithAllFields();
		authenticate();
		
		controller.createNewPatient(firstPatientToSave, new MockHttpServletRequest(), new MockHttpServletResponse());
		controller.createNewPatient(firstPatientToUpdate, new MockHttpServletRequest(), new MockHttpServletResponse());
		Patient gan123 = Context.getPatientService().getPatients("GAN123").get(0);
	}
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
