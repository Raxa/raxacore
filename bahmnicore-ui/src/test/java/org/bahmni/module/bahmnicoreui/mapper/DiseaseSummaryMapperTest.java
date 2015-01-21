package org.bahmni.module.bahmnicoreui.mapper;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.*;
import org.openmrs.module.bahmniemrapi.drugorder.dosinginstructions.FlexibleDosingInstructions;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)
public class DiseaseSummaryMapperTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DiseaseSummaryMapper.DATE_FORMAT);
    SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DiseaseSummaryMapper.DATE_TIME_FORMAT);
    String date1;
    String date2;
    String date3;
    String visit1Encounter1Date;
    String visit1Encounter2Date;
    String visit1Encounter3Date;

    @Before
    public void setUp() throws Exception {
        date1 = "2014-09-12";
        date2 = "2014-09-13";
        date3 = "2014-09-14";
        visit1Encounter1Date = date1 +" 12:30";
        visit1Encounter2Date = date1 + " 05:30";
        visit1Encounter3Date = date1 +" 07:30";
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void shouldMapObservationsToResponseFormat() throws ParseException {

        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> obsTable = diseaseSummaryMapper.mapObservations(createBahmniObsList(), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);
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
    public void shouldMapObservationsAndGroupByEncounters() throws ParseException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> obsTable = diseaseSummaryMapper.mapObservations(createBahmniObsList(), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_ENCOUNTER);
        assertNotNull(obsTable);
        assertEquals(5,obsTable.size());
        assertTrue(obsTable.containsKey(visit1Encounter1Date));

        Map<String, ConceptValue> visit1Encounter1Map = obsTable.get(visit1Encounter1Date);
        assertEquals(2, visit1Encounter1Map.size());
        assertEquals("101",visit1Encounter1Map.get("Temperature").getValue());
        assertEquals("90",visit1Encounter1Map.get("Pulse").getValue());

        Map<String, ConceptValue> visit1Encounter2Map = obsTable.get(visit1Encounter2Date);
        assertEquals(1, visit1Encounter2Map.size());
        assertEquals("102",visit1Encounter2Map.get("Temperature").getValue());

        Map<String, ConceptValue> visit1Encounter3Map = obsTable.get(visit1Encounter3Date);
        assertEquals(1, visit1Encounter3Map.size());
        assertEquals("103",visit1Encounter3Map.get("Temperature").getValue());

    }

    @Test
    public void shouldMapCodedConceptValues() throws ParseException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        List<BahmniObservation> bahmniObservations =  new ArrayList<>();

        Date visit1 = simpleDateFormat.parse(date1);
        bahmniObservations.add(createBahmniObservation(visit1,simpleDateTimeFormat.parse(date1 +" 12:30"),"Pulse",new EncounterTransaction.Concept("uuid-pulse","very high pulse")));

        Map<String, Map<String, ConceptValue>> obsTable = diseaseSummaryMapper.mapObservations(bahmniObservations, DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);

        Map<String, ConceptValue> dayValue = obsTable.get(date1);
        assertEquals(1, dayValue.size());
        assertEquals("very high pulse", dayValue.get("Pulse").getValue());

    }

    @Test
    public void shouldMapDrugOrders() throws ParseException, IOException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> drugOrderData = diseaseSummaryMapper.mapDrugOrders(mockDrugOrders(new String[]{"paracetamol", "2014-08-15","2014-08-15 05:30"}, new String[]{"paracetamol1", "2014-08-15","2014-08-15 06:30"},new String[]{"penicillin", "2014-09-11","2014-09-11 06:30"}), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);

        assertNotNull(drugOrderData);
        assertEquals(2, drugOrderData.size());
        Map<String, ConceptValue> firstDayValue = drugOrderData.get("2014-08-15");
        assertEquals(2, firstDayValue.size());
        assertEquals("paracetamol-500mg,10.0 mg,daily,SOS", firstDayValue.get("paracetamol").getValue());
        assertEquals("paracetamol1-500mg,10.0 mg,daily,SOS", firstDayValue.get("paracetamol1").getValue());

        Map<String, ConceptValue> secondDayValue = drugOrderData.get("2014-09-11");
        assertEquals(1, secondDayValue.size());
        assertEquals("penicillin-500mg,10.0 mg,daily,SOS", secondDayValue.get("penicillin").getValue());
    }

    @Test
    public void shouldMapDrugOrdersForEncounters() throws ParseException, IOException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> drugOrderData = diseaseSummaryMapper.mapDrugOrders(mockDrugOrders(new String[]{"paracetamol", "2014-08-15","2014-08-15 05:30"}, new String[]{"paracetamol1", "2014-08-15","2014-08-15 06:30"},new String[]{"penicillin", "2014-09-11","2014-09-11 06:30"}), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_ENCOUNTER);
        assertNotNull(drugOrderData);
        assertEquals(3, drugOrderData.size());
        Map<String, ConceptValue> firstEncounterValue = drugOrderData.get("2014-08-15 05:30");
        assertEquals(1, firstEncounterValue.size());
        assertEquals("paracetamol-500mg,10.0 mg,daily,SOS", firstEncounterValue.get("paracetamol").getValue());

        Map<String, ConceptValue> secondEncounterValue = drugOrderData.get("2014-08-15 06:30");
        assertEquals(1, secondEncounterValue.size());
        assertEquals("paracetamol1-500mg,10.0 mg,daily,SOS", secondEncounterValue.get("paracetamol1").getValue());

        Map<String, ConceptValue> thirdEncounterValue = drugOrderData.get("2014-09-11 06:30");
        assertEquals(1, thirdEncounterValue.size());
        assertEquals("penicillin-500mg,10.0 mg,daily,SOS", thirdEncounterValue.get("penicillin").getValue());
    }

    @Test
    public void shouldMapDrugOrdersWithFlexibleDosing() throws ParseException, IOException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> drugOrderData = diseaseSummaryMapper.mapDrugOrders(mockDrugOrdersWithFlexibleDosing(new String[]{"paracetamol", "2014-08-15", "2014-08-15 05:30"}, new String[]{"penicillin", "2014-09-11", "2014-09-11 05:30"}), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);

        assertNotNull(drugOrderData);
        assertEquals(2, drugOrderData.size());

        Map<String, ConceptValue> firstDayValue = drugOrderData.get("2014-08-15");
        assertEquals(1, firstDayValue.size());
        assertEquals("paracetamol-500mg,10.0 mg,1-0-1,SOS", firstDayValue.get("paracetamol").getValue());

        Map<String, ConceptValue> secondDayValue = drugOrderData.get("2014-09-11");
        assertEquals(1, secondDayValue.size());
        assertEquals("penicillin-500mg,10.0 mg,1-0-1,SOS", secondDayValue.get("penicillin").getValue());
    }

    @Test
    public void shouldMapDrugOrdersWithoutAnyExceptionsWhenThereIsNoData() throws ParseException, IOException {
        try{
            DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
            Map<String, Map<String, ConceptValue>> drugOrderData = diseaseSummaryMapper.mapDrugOrders(mockDrugOrdersWithoutAnyData(new String[]{"paracetamol", "2014-08-15", "2014-08-15 05:30"}, new String[]{"penicillin", "2014-09-11", "2014-09-11 05:30"}), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);

            assertNotNull(drugOrderData);
            assertEquals(2, drugOrderData.size());

            Map<String, ConceptValue> firstDayValue = drugOrderData.get("2014-08-15");
            assertEquals(1, firstDayValue.size());
            assertEquals("", firstDayValue.get("paracetamol").getValue());

            Map<String, ConceptValue> secondDayValue = drugOrderData.get("2014-09-11");
            assertEquals(1, secondDayValue.size());
            assertEquals("", secondDayValue.get("penicillin").getValue());
        }catch (Exception e){
            throw new RuntimeException("Should not throw any exception when drug orders dont have strength,dosage and freequency",e);
        }

    }

    private List<DrugOrder> mockDrugOrdersWithFlexibleDosing(String[]... drugInfoList) throws ParseException {
        List<DrugOrder> drugOrders = new ArrayList<>();
        for (String[] drugInfo : drugInfoList) {
            DrugOrder drugOrder = new DrugOrder();
            drugOrder.setConcept(createMRSConcept(drugInfo[0]));
            drugOrder.setEncounter(createEncounterWithVisitDateInfo(getDateFromString(drugInfo[1]), getDateTimeFromString(drugInfo[2])));
            drugOrder.setDrug(createDrugWithNameAndStrength(drugInfo[0], drugInfo[0] + "-500mg"));
            drugOrder.setDose(10.0);
            Concept doseUnits = new Concept();
            doseUnits.setFullySpecifiedName(new ConceptName("mg",Locale.getDefault()));
            drugOrder.setDoseUnits(doseUnits);
            drugOrder.setAsNeeded(true);
            drugOrder.setDosingInstructions("{\"instructions\":\"Before meals\",\"morningDose\":1,\"afternoonDose\":0,\"eveningDose\":1}");
            drugOrder.setDosingType(FlexibleDosingInstructions.class);
            drugOrders.add(drugOrder);
        }
        return drugOrders;
    }

    private List<DrugOrder> mockDrugOrdersWithoutAnyData(String[]... drugInfoList) throws ParseException {
        List<DrugOrder> drugOrders = new ArrayList<>();
        for (String[] drugInfo : drugInfoList) {
            DrugOrder drugOrder = new DrugOrder();
            drugOrder.setConcept(createMRSConcept(drugInfo[0]));
            drugOrder.setEncounter(createEncounterWithVisitDateInfo(getDateFromString(drugInfo[1]), getDateFromString(drugInfo[2])));
            drugOrder.setDrug(createDrugWithNameAndStrength(drugInfo[0], ""));
            drugOrders.add(drugOrder);
        }
        return drugOrders;
    }

    @Test
    public void shouldMapLabOrders() throws ParseException {
        DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
        Map<String, Map<String, ConceptValue>> labOrderData = diseaseSummaryMapper.mapLabResults(mockLabOrders(), DiseaseSummaryMapper.RESULT_TABLE_GROUP_BY_VISITS);

        assertNotNull(labOrderData);
        assertEquals(2, labOrderData.size());

        Map<String, ConceptValue> firstDayValue = labOrderData.get("2014-07-22");
        assertEquals(1, firstDayValue.size());
        assertEquals("120", firstDayValue.get("Blood glucose").getValue());

        Map<String, ConceptValue> secondDayValue = labOrderData.get("2014-07-23");
        assertEquals(2, secondDayValue.size());
        assertEquals("140", secondDayValue.get("Blood glucose").getValue());
        assertEquals("3.0", secondDayValue.get("serum creatinine").getValue());

    }

    private List<LabOrderResult> mockLabOrders() throws ParseException {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        labOrderResults.add(createLabOrder("2014-07-22","Blood glucose","120"));
        labOrderResults.add(createLabOrder("2014-07-23","Blood glucose","140"));
        labOrderResults.add(createLabOrder("2014-07-23","serum creatinine","3.0"));
        return labOrderResults;
    }

    private LabOrderResult createLabOrder(String visitDate, String conceptName, String value) throws ParseException {

        LabOrderResult labOrderResult = new LabOrderResult();
        Date date = simpleDateFormat.parse(visitDate);
        labOrderResult.setVisitStartTime(date);
        labOrderResult.setResult(value);
        labOrderResult.setTestName(conceptName);
        return labOrderResult;
    }


    private List<DrugOrder> mockDrugOrders(String[]... drugInfoList) throws ParseException {
        List<DrugOrder> drugOrders = new ArrayList<>();
        for (String[] drugInfo : drugInfoList) {
            DrugOrder drugOrder = new DrugOrder();
            drugOrder.setConcept(createMRSConcept(drugInfo[0]));
            drugOrder.setEncounter(createEncounterWithVisitDateInfo(getDateFromString(drugInfo[1]), getDateTimeFromString(drugInfo[2])));
            drugOrder.setDrug(createDrugWithNameAndStrength(drugInfo[0], drugInfo[0] + "-500mg"));
            drugOrder.setDose(10.0);
            Concept doseUnits = new Concept();
            doseUnits.setFullySpecifiedName(new ConceptName("mg",Locale.getDefault()));
            drugOrder.setDoseUnits(doseUnits);
            drugOrder.setAsNeeded(true);
            OrderFrequency frequency = new OrderFrequency();
            frequency.setConcept(createMRSConcept("daily"));
            drugOrder.setFrequency(frequency);
            drugOrders.add(drugOrder);
        }
        return drugOrders;
    }

    private Drug createDrugWithNameAndStrength(String drugName, String strength) {
        Drug drug = new Drug();
        drug.setName(drugName);
        drug.setStrength(strength);
        drug.setConcept(createMRSConcept(drugName));
        return drug;
    }

    private Encounter createEncounterWithVisitDateInfo(Date visitDate, Date encounterDate) {
        Encounter encounter = new Encounter();
        Visit visit = new Visit();
        visit.setStartDatetime(visitDate);
        encounter.setVisit(visit);
        encounter.setEncounterDatetime(encounterDate);

        return encounter;
    }

    private Date getDateFromString(String dateString) throws ParseException {
        return simpleDateFormat.parse(dateString);
    }

    private Date getDateTimeFromString(String dateString) throws ParseException {
        return simpleDateTimeFormat.parse(dateString);
    }

    private Concept createMRSConcept(String drugName) {
        Concept concept =  new Concept();
        concept.setFullySpecifiedName(new ConceptName(drugName, Locale.getDefault()));
        return concept;
    }


    private List<BahmniObservation> createBahmniObsList() throws ParseException {
        List<BahmniObservation> bahmniObservations =  new ArrayList<>();
        Date visit1 = simpleDateFormat.parse(date1);
        Date visit2 = simpleDateFormat.parse(date2);
        Date visit3 = simpleDateFormat.parse(date3);

        Date encounter1 = simpleDateTimeFormat.parse(visit1Encounter1Date);
        Date encounter2 = simpleDateTimeFormat.parse(visit1Encounter2Date);
        Date encounter3 = simpleDateTimeFormat.parse(visit1Encounter3Date);

        bahmniObservations.add(createBahmniObservation(visit1,encounter1, "Temperature","101"));
        bahmniObservations.add(createBahmniObservation(visit1,encounter1, "Pulse","90"));

        bahmniObservations.add(createBahmniObservation(visit1,encounter2, "Temperature","102"));
        bahmniObservations.add(createBahmniObservation(visit1,encounter3, "Temperature","103"));

        bahmniObservations.add(createBahmniObservation(visit2,simpleDateTimeFormat.parse(date2 +" 12:30"),"Pulse","100"));
        bahmniObservations.add(createBahmniObservation(visit3,simpleDateTimeFormat.parse(date3 +" 12:30"),"bp","120"));
        return bahmniObservations;
    }


    private BahmniObservation createBahmniObservation(Date visitStartTime, Date encounterDateTime, String conceptName, Object value) {
        BahmniObservation bahmniObservation = new BahmniObservation();
        bahmniObservation.setVisitStartDateTime(visitStartTime);
        bahmniObservation.setEncounterDateTime(encounterDateTime);
        bahmniObservation.setConcept(new EncounterTransaction.Concept("uuid-"+conceptName,conceptName));
        bahmniObservation.setValue(value);
        return bahmniObservation;
    }
}