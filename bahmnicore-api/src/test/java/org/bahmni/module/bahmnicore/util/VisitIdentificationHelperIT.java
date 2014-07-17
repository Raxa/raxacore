package org.bahmni.module.bahmnicore.util;

import org.bahmni.module.bahmnicore.util.VisitIdentificationHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class VisitIdentificationHelperIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    VisitService visitService;

    @Autowired
    PatientService patientService;

    VisitIdentificationHelper visitIdentificationHelper;

    @Before
    public void setUp() {
        visitIdentificationHelper = new VisitIdentificationHelper(visitService);
    }

//    @Test
//    @Ignore("Mujir/Vinay - TODO - need to look into it")
//    public void shouldGetVisitEncompassingASpecificDate() throws Exception {
//        executeDataSet("visitIdentificationHelper.xml");
//
//        Patient patient = patientService.getPatient(1);
//        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 01:00:00");
//        System.out.println(acessionDate);
//
//        DateTime startTime = new DateTime(acessionDate);
//        Visit visit = visitIdentificationHelper.getVisitForPatientWithinDates(patient, startTime.toDate());
//        assertEquals(2, visit.getId().intValue());
//
//        visit = visitIdentificationHelper.getVisitForPatientForNearestStartDate(patient, startTime.toDate());
//        assertEquals(3, visit.getId().intValue());
//    }

    @Test
    @Ignore("Mujir/Vinay - talked to BAs. this scenario would never occur till we get to IPD visit types. Do not delete the test. Fix this test when we do IPD visits.")
    public void shouldFetchTheExistingVisit() throws Exception {
        executeDataSet("visitIdentificationHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-11 01:00:00");

        Visit visit = visitIdentificationHelper.getVisitFor(patient, acessionDate, "LAB VISIT");
        assertEquals(1, visit.getId().intValue());

        acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 01:00:00");
        visit = visitIdentificationHelper.getVisitFor(patient, acessionDate, "LAB VISIT");
        assertEquals(2, visit.getId().intValue());
    }

    @Test
    @Ignore("Mujir/Vinay - talked to BAs. this scenario would never occur till we get to IPD visit types. Do not delete the test. Fix this test when we do IPD visits.")
    public void shouldInitializeNewVisitWhenNextVisitWithIn24Hours() throws Exception {
        executeDataSet("visitIdentificationHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 05:59:59");

        Visit visit = visitIdentificationHelper.getVisitFor(patient, acessionDate, "LAB VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());
    }

    @Test
    @Ignore
    public void shouldInitializeNewVisitWhenNextVisitNotWithIn24Hours() throws Exception {
        executeDataSet("visitIdentificationHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-18 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-18 23:59:59");

        Visit visit = visitIdentificationHelper.getVisitFor(patient, acessionDate, "LAB VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());

    }

    @Test
    @Ignore
    public void shouldInitializeNewVisitWhenNextVisitDoesNotExist() throws Exception {
        executeDataSet("visitIdentificationHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-21 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-21 23:59:59");

        Visit visit = visitIdentificationHelper.getVisitFor(patient, acessionDate, "LAB VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());
    }

//        V1	10-Feb	10:00		12-Feb	6:00
//        V2	12-Feb	8:00		13-Feb	2:00
//        V3	13-Feb	6:00		14-Feb	5:00
//        v4  14th feb 6:00


}
