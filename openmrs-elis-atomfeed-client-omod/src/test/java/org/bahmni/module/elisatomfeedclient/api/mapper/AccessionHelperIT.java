package org.bahmni.module.elisatomfeedclient.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
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
public class AccessionHelperIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    VisitService visitService;

    @Autowired
    PatientService patientService;

    AccessionHelper accessionHelper;

    @Before
    public void setUp() {
        accessionHelper = new AccessionHelper(null, null, visitService, null, null, null, null, null);
    }

    @Test
    public void shouldGetVisitEncompassingASpecificDate() throws Exception {
        executeDataSet("accessionHelper.xml");

        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 01:00:00");
        System.out.println(acessionDate);

        DateTime startTime = new DateTime(acessionDate);
        Visit visit = accessionHelper.getVisitForPatientWithinDates(patient, startTime.toDate());
        assertEquals(2, visit.getId().intValue());

        visit = accessionHelper.getVisitForPatientForNearestStartDate(patient, startTime.toDate());
        assertEquals(3, visit.getId().intValue());
    }

    @Test
    public void shouldFetchTheExistingVisit() throws Exception {
        executeDataSet("accessionHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-11 01:00:00");

        Visit visit = accessionHelper.findOrInitializeVisit(patient, acessionDate, "LAB_VISIT");
        assertEquals(1, visit.getId().intValue());

        acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 01:00:00");
        visit = accessionHelper.findOrInitializeVisit(patient, acessionDate, "LAB_VISIT");
        assertEquals(2, visit.getId().intValue());
    }

    @Test
    public void shouldInitializeNewVisitWhenNextVisitWithIn24Hours() throws Exception {
        executeDataSet("accessionHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-13 05:59:59");

        Visit visit = accessionHelper.findOrInitializeVisit(patient, acessionDate, "LAB_VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());

    }

    @Test
    public void shouldInitializeNewVisitWhenNextVisitNotWithIn24Hours() throws Exception {
        executeDataSet("accessionHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-18 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-18 23:59:59");

        Visit visit = accessionHelper.findOrInitializeVisit(patient, acessionDate, "LAB_VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());

    }

    @Test
    public void shouldInitializeNewVisitWhenNextVisitDoesNotExist() throws Exception {
        executeDataSet("accessionHelper.xml");
        Patient patient = patientService.getPatient(1);
        Date acessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-21 03:00:00");
        Date stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-21 03:00:01");

        Visit visit = accessionHelper.findOrInitializeVisit(patient, acessionDate, "LAB_VISIT");

        assertNull(visit.getId());
        assertEquals(acessionDate, visit.getStartDatetime());
        assertEquals(stopTime, visit.getStopDatetime());

    }

//        V1	10-Feb	10:00		12-Feb	6:00
//        V2	12-Feb	8:00		13-Feb	2:00
//        V3	13-Feb	6:00		14-Feb	5:00
//        v4  14th feb 6:00


}
