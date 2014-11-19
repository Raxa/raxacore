package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.test.BaseContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:webModuleApplicationContext.xml","classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientPersisterIT extends BaseContextSensitiveTest {

    private String path;
    private PatientPersister patientPersister = new PatientPersister();

    @Before
    public void setUp() throws Exception {
        path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);

        Context.authenticate("admin", "test");
        UserContext userContext = Context.getUserContext();
        patientPersister.init(userContext);
    }

    @Test
    public void save_patient_row() {
        PatientRow patientRow = patientRow("Ram", "Laxman", "Kumar", "1999-08-8", "Male", "reg-no", addressParts("galli", "shahar", "state", "desh", "100001"), attibutesList("ram", "farmer"));
        RowResult<PatientRow> patientRowResult = patientPersister.persist(patientRow);

        assertTrue("should have persisted the patient row", patientRowResult.isSuccessful());
    }

    private PatientRow patientRow(String firstName, String middleName, String lastName, String birthdate, String gender, String registrationNumber, List<KeyValue> addressParts, List<KeyValue> attributes) {
        PatientRow patientRow = new PatientRow();
        patientRow.firstName = firstName;
        patientRow.middleName = middleName;
        patientRow.lastName = lastName;
        patientRow.birthdate = birthdate;
        patientRow.gender = gender;
        patientRow.registrationNumber = registrationNumber;
        patientRow.addressParts = addressParts;
        patientRow.attributes = attributes;
        return patientRow;
    }

    private List<KeyValue> addressParts(final String street, final String city, final String state, final String country, final String postalCode) {
        List<KeyValue> addressParts = new ArrayList<KeyValue>() {{
            add(new KeyValue("Street", street));
            add(new KeyValue("City", city));
            add(new KeyValue("State", state));
            add(new KeyValue("Country", country));
            add(new KeyValue("Postal Code", postalCode));
        }};
        return addressParts;
    }

    private List<KeyValue> attibutesList(final String localName, final String occupation) {
        List<KeyValue> attributes = new ArrayList<KeyValue>() {{
            add(new KeyValue("familyNameLocal", localName));
            add(new KeyValue("occupation", occupation));
        }};
        return attributes;
    }

}