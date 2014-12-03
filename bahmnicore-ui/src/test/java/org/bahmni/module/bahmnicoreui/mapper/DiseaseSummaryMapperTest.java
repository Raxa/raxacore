package org.bahmni.module.bahmnicoreui.mapper;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DiseaseSummaryMapperTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DiseaseSummaryMapper.DATE_FORMAT);
    String date1;
    String date2;
    String date3;

    @Before
    public void setUp() throws Exception {
        date1 = "2014-09-12";
        date2 = "2014-09-13";
        date3 = "2014-09-14";
    }

    @Test
    public void shouldMapObservationsToResponseFormat() throws ParseException {

        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> obsTable = diseaseSummaryMapper.mapObservations(createBahmniObsList());
        assertNotNull(obsTable);
        assertEquals(3, obsTable.size());
        Map<String, ConceptValue> firstDayValue = obsTable.get(date1);
        assertEquals(2, firstDayValue.size());
        assertEquals("101", firstDayValue.get("Temperature").getValue());
        assertEquals("90", firstDayValue.get("Pulse").getValue());

        Map<String, ConceptValue> secondDayValue = obsTable.get(date2);
        assertEquals(1, secondDayValue.size());
        assertEquals("100", secondDayValue.get("Pulse").getValue());

        Map<String, ConceptValue> thirdDayValue = obsTable.get(date3);
        assertEquals(1, thirdDayValue.size());
        assertEquals("120", thirdDayValue.get("bp").getValue());

    }

    @Test
    public void shouldMapOnlyLatestObservationIfMultipleObservationForSameConceptExistInOneVisit() throws ParseException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        List<BahmniObservation> bahmniObservations =  new ArrayList<>();

        Date visit1 = simpleDateFormat.parse(date1);
        bahmniObservations.add(createBahmniObservation(visit1,"Pulse","90"));
        bahmniObservations.add(createBahmniObservation(visit1,"Pulse","100"));

        Map<String, Map<String, ConceptValue>> obsTable = diseaseSummaryMapper.mapObservations(bahmniObservations);

        Map<String, ConceptValue> dayValue = obsTable.get(date1);
        assertEquals(1, dayValue.size());
        assertEquals("100", dayValue.get("Pulse").getValue());    //should write latest observation if multiple observation for same concept exist in one visit.

    }

    private List<BahmniObservation> createBahmniObsList() throws ParseException {
        List<BahmniObservation> bahmniObservations =  new ArrayList<>();
        Date visit1 = simpleDateFormat.parse(date1);
        Date visit2 = simpleDateFormat.parse(date2);
        Date visit3 = simpleDateFormat.parse(date3);

        bahmniObservations.add(createBahmniObservation(visit1,"Temperature","101"));
        bahmniObservations.add(createBahmniObservation(visit1,"Pulse","90"));
        bahmniObservations.add(createBahmniObservation(visit2,"Pulse","100"));
        bahmniObservations.add(createBahmniObservation(visit3,"bp","120"));
        return bahmniObservations;
    }

    private BahmniObservation createBahmniObservation(Date visitStartTime, String conceptName, String value) {
        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setVisitStartDateTime(visitStartTime);
        bahmniObservation.setConcept(new EncounterTransaction.Concept("uuid-"+conceptName,conceptName));
        bahmniObservation.setValue(value);
        return bahmniObservation;
    }
}