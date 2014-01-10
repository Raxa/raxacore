package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BahmniCoreApiProperties;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.dao.BahmniPatientDao;
import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:applicationContext-Test.xml"}, inheritLocations = true)
public class BahmniPatientDaoImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniPatientDao bahmniPatientDao;

    @Before
    public void setup() throws Exception {
        executeDataSet("apiTestData.xml");
    }

    @Test
    public void shouldSearchByPatientIdentifier() {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("GAN200001", "", "", 100, 0);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient.getUuid());
        assertEquals("GAN200001", patient.getIdentifier());
        assertEquals("Horatio", patient.getGivenName());
        assertEquals("Sinha", patient.getFamilyName());
        assertEquals("M", patient.getGender());
        assertEquals("1983-01-30", patient.getBirthDate().toString());
        assertEquals("Ramgarh", patient.getCityVillage());
        assertEquals("2008-08-15 15:57:09.0", patient.getDateCreated().toString());
        assertEquals(null, patient.getDeathDate());
    }

    @Test
    public void shouldSearchByName() {

        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "Horatio", "", 100, 0);

        assertEquals(2, patients.size());
        PatientResponse patient1 = patients.get(0);
        PatientResponse patient2 = patients.get(1);
        List<String> uuids = asList("341b4e41-790c-484f-b6ed-71dc8da222db", "86526ed5-3c11-11de-a0ba-001e378eb67a");

        assertTrue(uuids.contains(patient1.getUuid()));
        assertTrue(uuids.contains(patient2.getUuid()));

        assertEquals("Horatio", patient1.getGivenName());
        assertEquals("Horatio", patient2.getGivenName());
    }

    @Test
    public void shouldSearchAcrossFirstNameAndLastName() {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "Horati Sinha", "", 100, 0);

        assertEquals(1, patients.size());
        PatientResponse patient1 = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient1.getUuid());
        assertEquals("Horatio", patient1.getGivenName());
        assertEquals("Sinha", patient1.getFamilyName());
    }

    @Test
    public void shouldSearchByVillage() {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "", "Ramgarh", 100, 0);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient.getUuid());
        assertEquals("GAN200001", patient.getIdentifier());
        assertEquals("Horatio", patient.getGivenName());
        assertEquals("Sinha", patient.getFamilyName());
        assertEquals("M", patient.getGender());
        assertEquals("1983-01-30", patient.getBirthDate().toString());
        assertEquals("Ramgarh", patient.getCityVillage());
        assertEquals("2008-08-15 15:57:09.0", patient.getDateCreated().toString());
        assertEquals(null, patient.getDeathDate());
    }

    @Test
    public void shouldSearchByNameAndVillage() {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "Sin", "Ramgarh", 100, 0);
        assertEquals(1, patients.size());
        PatientResponse patient = patients.get(0);
        assertEquals("341b4e41-790c-484f-b6ed-71dc8da222db", patient.getUuid());
        assertEquals("GAN200001", patient.getIdentifier());
        assertEquals("Horatio", patient.getGivenName());
        assertEquals("Sinha", patient.getFamilyName());
        assertEquals("M", patient.getGender());
        assertEquals("1983-01-30", patient.getBirthDate().toString());
        assertEquals("Ramgarh", patient.getCityVillage());
        assertEquals("2008-08-15 15:57:09.0", patient.getDateCreated().toString());
        assertEquals(null, patient.getDeathDate());
    }

    @Test
    public void shouldSortResultsByCreationDate() {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "Sinha", "", 100, 0);
        assertEquals(2, patients.size());
        assertEquals("Sinha", patients.get(0).getFamilyName());
        assertEquals("Sinha", patients.get(0).getFamilyName());
    }

    @Test
    public void shouldReturnResultAfterGivenOffset() throws Exception {
        List<PatientResponse> patients = bahmniPatientDao.getPatients("", "Sinha", "", 100, 1);
        assertEquals(1, patients.size());

        patients = bahmniPatientDao.getPatients("", "Sinha", "", 100, 2);
        assertEquals(0, patients.size());
    }
}

@Component
class MockBahmniCoreApiProperties implements BahmniCoreApiProperties {
    @Override
    public String getImageDirectory() {
        return null;
    }
    @Override
    public ExecutionMode getExecutionMode() {
        return null;
    }
    @Override
    public String getPatientImagesUrl() {
        return null;
    }

    @Override
    public String getDocumentBaseDirectory() {
        return null;
    }
}



