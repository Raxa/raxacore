package org.bahmni.module.admin.observation.handler;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.csv.service.FormFieldPathGeneratorService;
import org.bahmni.module.admin.observation.CSVObservationHelper;
import org.bahmni.form2.service.FormFieldPathService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openmrs.api.APIException;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(CSVObservationHelper.class)
@RunWith(PowerMockRunner.class)
public class Form2CSVObsHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Form2CSVObsHandler form2CSVObsHandler;
    private CSVObservationHelper csvObservationHelper;
    private FormFieldPathService formFieldPathService;
    private FormFieldPathGeneratorService formFieldPathGeneratorService;

    @Before
    public void setUp() {
        initMocks(this);
        csvObservationHelper = mock(CSVObservationHelper.class);
        formFieldPathService = mock(FormFieldPathService.class);
        formFieldPathGeneratorService = mock(FormFieldPathGeneratorService.class);
    }

    @Test
    public void shouldFilterForm2CSVObs() {
        final KeyValue form1CSVObservation = new KeyValue("Vitals.Height", "100");
        final KeyValue form2CSVObservation = new KeyValue("form2.Vitals.Height", "100");

        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = asList(form1CSVObservation, form2CSVObservation);

        when(csvObservationHelper.isForm2Type(form1CSVObservation)).thenReturn(false);
        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, null, null);

        final List<KeyValue> form2CSVObs = form2CSVObsHandler.getRelatedCSVObs(encounterRow);

        assertEquals(1, form2CSVObs.size());
        assertEquals(form2CSVObservation, form2CSVObs.get(0));
        verify(csvObservationHelper).isForm2Type(form1CSVObservation);
        verify(csvObservationHelper).isForm2Type(form2CSVObservation);
    }

    @Test
    public void shouldVerifyCreateObservationsIsCalled() throws ParseException {
        final KeyValue form1CSVObservation = new KeyValue("Vitals.Height", "100");
        final KeyValue form2CSVObservation = new KeyValue("form2.Vitals.Height", "100");

        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = asList(form1CSVObservation, form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form1CSVObservation)).thenReturn(false);
        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);

        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Vitals", "Height"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        doNothing().when(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        doNothing().when(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), any(KeyValue.class), anyListOf(String.class));
        when(formFieldPathService.getFormFieldPath(asList("Vitals", "Height"))).thenReturn("Vitals.1/1-0");

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        form2CSVObsHandler.handle(encounterRow);

        verify(csvObservationHelper).isForm2Type(form1CSVObservation);
        verify(csvObservationHelper).isForm2Type(form2CSVObservation);
        verify(csvObservationHelper).getCSVHeaderParts(form2CSVObservation);
        verify(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        verify(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), any(KeyValue.class), anyListOf(String.class));
    }

    @Test
    public void shouldVerifyCreateObservationIsNotCalledWhenAnEmptyValueIsGiven() throws ParseException {
        final KeyValue form1CSVObservation = new KeyValue("Vitals.Height", "100");
        final KeyValue form2CSVObservation = new KeyValue("form2.Vitals.Height", "");

        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = asList(form1CSVObservation, form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form1CSVObservation)).thenReturn(false);
        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        form2CSVObsHandler.handle(encounterRow);

        verify(csvObservationHelper).isForm2Type(form1CSVObservation);
        verify(csvObservationHelper).isForm2Type(form2CSVObservation);
        verify(csvObservationHelper, never()).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), any(KeyValue.class), anyListOf(String.class));
    }

    @Test
    public void shouldThrowAPIExceptionIfNoConceptProvidedWithCSVHeader() throws ParseException {
        final KeyValue form2CSVObservation = new KeyValue("form2.Vitals", "100");
        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = singletonList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Vitals"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        expectedException.expect(APIException.class);
        expectedException.expectMessage(format("No concepts found in %s", form2CSVObservation.getKey()));

        form2CSVObsHandler.handle(encounterRow);
    }

    @Test
    public void shouldCreateObsForMultiSelectConcept() throws ParseException {
        final KeyValue form2CSVObservation = new KeyValue("form2.HIV_History.PresentConditions", "Asymptomatic|Herpes Zoster");

        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = asList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);

        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "HIV_History", "PresentConditions"));
        final List<String> obsValues = new ArrayList<>(Arrays.asList("Asymptomatic", "Herpes Zoster"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        when(csvObservationHelper.getMultiSelectObs(form2CSVObservation)).thenReturn(obsValues);
        doNothing().when(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        doNothing().when(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), anyListOf(KeyValue.class), anyListOf(String.class));
        when(formFieldPathService.isMultiSelectObs(asList("HIV_History", "PresentConditions"))).thenReturn(true);
        when(formFieldPathService.isValidCSVHeader(asList("HIV_History", "PresentConditions"))).thenReturn(true);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        form2CSVObsHandler.handle(encounterRow, true);
        verify(csvObservationHelper).isForm2Type(form2CSVObservation);
        verify(csvObservationHelper).getCSVHeaderParts(form2CSVObservation);
        verify(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        verify(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), anyListOf(KeyValue.class), anyListOf(String.class));
    }

    @Test
    public void shouldCreateObsForAddmoreConcept() throws ParseException {
        final KeyValue form2CSVObservation = new KeyValue("form2.TB.Method of confirmation", "Smear|Hain test");

        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = asList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);

        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "TB", "Method of confirmation"));
        final List<String> obsValues = new ArrayList<>(Arrays.asList("Smear", "Hain test"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        when(csvObservationHelper.getMultiSelectObs(form2CSVObservation)).thenReturn(obsValues);
        doNothing().when(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        doNothing().when(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), anyListOf(KeyValue.class), anyListOf(String.class));
        when(formFieldPathService.isAddmore(asList("TB", "Method of confirmation"))).thenReturn(true);
        when(formFieldPathService.isValidCSVHeader(asList("TB", "Method of confirmation"))).thenReturn(true);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        form2CSVObsHandler.handle(encounterRow, true);
        verify(csvObservationHelper).isForm2Type(form2CSVObservation);
        verify(csvObservationHelper).getCSVHeaderParts(form2CSVObservation);
        verify(csvObservationHelper).verifyNumericConceptValue(form2CSVObservation, headerParts);
        verify(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), anyListOf(KeyValue.class), anyListOf(String.class));
    }

    @Test
    public void shouldThrowAPIExceptionIfMandatoryObsIsEmpty() throws ParseException {
        final KeyValue form2CSVObservation = new KeyValue("form2.Vitals", "");
        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = singletonList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Vitals"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        when(formFieldPathService.isMandatory(asList("Vitals"))).thenReturn(true);
        when(formFieldPathService.isValidCSVHeader(asList("Vitals"))).thenReturn(true);

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        expectedException.expect(APIException.class);
        expectedException.expectMessage("Empty value provided for mandatory field Vitals");

        form2CSVObsHandler.handle(encounterRow, true);
    }

    @Test
    public void shouldThrowAPIExceptionIfFutureDateIsProvided() throws ParseException {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setUuid("UUID");
        observation.setConcept(new EncounterTransaction.Concept());
        observation.setValue("2099-12-31");
        observation.getConcept().setDataType("Date");

        final KeyValue form2CSVObservation = new KeyValue("form2.TB.Past Visit Date", "2099-12-31");
        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = singletonList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "TB", "Past Visit Date"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        when(formFieldPathService.isAllowFutureDates(asList("TB", "Past Visit Date"))).thenReturn(false);
        when(formFieldPathService.isValidCSVHeader(asList("TB", "Past Visit Date"))).thenReturn(true);

        PowerMockito.mockStatic(CSVObservationHelper.class);
        when(csvObservationHelper.getLastItem(anyListOf(EncounterTransaction.Observation.class))).thenReturn(observation);
        when(csvObservationHelper.getLastItem(eq(asList("TB", "Past Visit Date")))).thenReturn("Past Visit Date");

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        expectedException.expect(APIException.class);
        expectedException.expectMessage("Future date [2099-12-31] is not allowed for [Past Visit Date]");

        form2CSVObsHandler.handle(encounterRow, true );

        verify(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), any(KeyValue.class), anyListOf(String.class));
    }

    @Test
    public void shouldThrowAPIExceptionIfCSVHeaderIsInvalid() throws ParseException {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setUuid("UUID");
        observation.setConcept(new EncounterTransaction.Concept());
        observation.setValue("2099-12-31");
        observation.getConcept().setDataType("Date");

        final KeyValue form2CSVObservation = new KeyValue("form2.TB.Past Visit Date", "2099-12-31");
        final EncounterRow encounterRow = new EncounterRow();
        encounterRow.obsRows = singletonList(form2CSVObservation);
        encounterRow.encounterDateTime = "2019-11-11";

        when(csvObservationHelper.isForm2Type(form2CSVObservation)).thenReturn(true);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "TB", "Past Visit Date"));
        when(csvObservationHelper.getCSVHeaderParts(form2CSVObservation)).thenReturn(headerParts);
        when(formFieldPathService.isValidCSVHeader(asList("TB", "Past Visit Date"))).thenReturn(false);

        PowerMockito.mockStatic(CSVObservationHelper.class);
        when(csvObservationHelper.getLastItem(anyListOf(EncounterTransaction.Observation.class))).thenReturn(observation);
        when(csvObservationHelper.getLastItem(eq(asList("TB", "Past Visit Date")))).thenReturn("Past Visit Date");

        form2CSVObsHandler = new Form2CSVObsHandler(csvObservationHelper, formFieldPathService, formFieldPathGeneratorService);

        expectedException.expect(APIException.class);
        expectedException.expectMessage("No concepts found in form2.TB.Past Visit Date");

        form2CSVObsHandler.handle(encounterRow, true );

        verify(csvObservationHelper).createObservations(anyListOf(EncounterTransaction.Observation.class),
                any(Date.class), any(KeyValue.class), anyListOf(String.class));
    }
}
