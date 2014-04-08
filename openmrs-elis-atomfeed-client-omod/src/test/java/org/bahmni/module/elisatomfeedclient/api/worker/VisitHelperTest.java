package org.bahmni.module.elisatomfeedclient.api.worker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitHelperTest {
    @Mock
    VisitService visitService;
    private VisitHelper visitHelper;

    @Before
    public void setUp() throws ParseException {
        initMocks(this);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(sampleVisits());
    }

    private List<Visit> sampleVisits() throws ParseException {
        List<Visit> activeVisits = new ArrayList<>();
        activeVisits.add(createVisitWithDateTime("05/04/2014"));
        activeVisits.add(createVisitWithDateTime("06/04/2014"));
        activeVisits.add(createVisitWithDateTime("07/04/2014"));
        return activeVisits;
    }

    private Visit createVisitWithDateTime(String date) throws ParseException {
        Visit visit = new Visit();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = sdf.parse(date);
        visit.setStartDatetime(d);
        return visit;
    }

    @Test
    public void shouldGetLatestFromAllVisits() throws ParseException {
        visitHelper = new VisitHelper(visitService);
        Visit latestVisit = visitHelper.getLatestVisit(new Patient());
        assertEquals(createVisitWithDateTime("07/04/2014").getStartDatetime(),latestVisit.getStartDatetime());
    }
}
