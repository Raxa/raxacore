package org.bahmni.module.admin.csv.service;

import org.bahmni.csv.KeyValue;
import org.bahmni.form2.service.FormFieldPathService;
import org.bahmni.module.admin.observation.CSVObservationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@PrepareForTest(CSVObservationHelper.class)
@RunWith(PowerMockRunner.class)
public class FormFieldPathGeneratorServiceTest {

    private FormFieldPathService formFieldPathService;
    private FormFieldPathGeneratorService formFieldPathGeneratorService;

    @Before
    public void setUp() {
        formFieldPathService = mock(FormFieldPathService.class);
        formFieldPathGeneratorService = new FormFieldPathGeneratorService(formFieldPathService);
    }

    @Test
    public void shouldNotSetFormFieldPathForEmptyObserevations() {
        List<EncounterTransaction.Observation> form2Observations = asList();
        List<String> form2CSVHeaderParts = null;
        formFieldPathGeneratorService.setFormNamespaceAndFieldPath(form2Observations, form2CSVHeaderParts);

        assertEquals(0, form2Observations.size());
    }

    @Test
    public void shouldSetFormFieldPathForObserevation() {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        observation.setUuid("UUID");
        observation.setConcept(new EncounterTransaction.Concept());
        observation.setValue("Dummy Value");

        List<EncounterTransaction.Observation> form2Observations = asList(observation);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Vitals", "Height"));

        PowerMockito.mockStatic(CSVObservationHelper.class);
        when(CSVObservationHelper.getLastItem(anyListOf(EncounterTransaction.Observation.class))).thenReturn(observation);
        when(formFieldPathService.getFormFieldPath(anyListOf(String.class))).thenReturn("Vitals.1/1-0");

        formFieldPathGeneratorService.setFormNamespaceAndFieldPath(form2Observations, headerParts);

        assertEquals("Vitals.1/1-0", form2Observations.get(0).getFormFieldPath());
        assertEquals("Bahmni", form2Observations.get(0).getFormNamespace());
    }

    @Test
    public void shouldSetFormFieldPathForMultiSelectObserevation() {
        EncounterTransaction.Observation observation1 = new EncounterTransaction.Observation();
        observation1.setUuid("UUID1");
        observation1.setConcept(new EncounterTransaction.Concept());
        observation1.setValue("Cough");

        EncounterTransaction.Observation observation2 = new EncounterTransaction.Observation();
        observation2.setUuid("UUID2");
        observation2.setConcept(new EncounterTransaction.Concept());
        observation2.setValue("Fever");

        final KeyValue obs1 = new KeyValue("Covid.Symptoms", "Cough");
        final KeyValue obs2 = new KeyValue("Covid.Symptoms", "Fever");

        List<EncounterTransaction.Observation> form2Observations = asList(observation1, observation2);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Covid", "Symptoms"));

        PowerMockito.mockStatic(CSVObservationHelper.class);
        when(CSVObservationHelper.getLastItem(anyListOf(EncounterTransaction.Observation.class))).thenReturn(observation1);
        when(formFieldPathService.getFormFieldPath(anyListOf(String.class))).thenReturn("Covid.1/1-0");

        formFieldPathGeneratorService.setFormNamespaceAndFieldPathForMultiSelectObs(form2Observations, headerParts, asList(obs1,obs2));

        assertEquals("Covid.1/1-0", form2Observations.get(0).getFormFieldPath());
        assertEquals("Covid.1/1-0", form2Observations.get(1).getFormFieldPath());
        assertEquals("Bahmni", form2Observations.get(0).getFormNamespace());
        assertEquals("Bahmni", form2Observations.get(1).getFormNamespace());
    }

    @Test
    public void shouldSetFormFieldPathForAddmoreObserevation() {
        EncounterTransaction.Observation observation1 = new EncounterTransaction.Observation();
        observation1.setUuid("UUID1");
        observation1.setConcept(new EncounterTransaction.Concept());
        observation1.setValue("Cough");

        EncounterTransaction.Observation observation2 = new EncounterTransaction.Observation();
        observation2.setUuid("UUID2");
        observation2.setConcept(new EncounterTransaction.Concept());
        observation2.setValue("Fever");

        final KeyValue obs1 = new KeyValue("Covid.Symptoms", "Cough");
        final KeyValue obs2 = new KeyValue("Covid.Symptoms", "Fever");

        List<EncounterTransaction.Observation> form2Observations = asList(observation1, observation2);
        final List<String> headerParts = new ArrayList<>(Arrays.asList("form2", "Covid", "Symptoms"));

        PowerMockito.mockStatic(CSVObservationHelper.class);
        when(CSVObservationHelper.getLastItem(anyListOf(EncounterTransaction.Observation.class))).thenReturn(observation1);
        when(formFieldPathService.getFormFieldPath(anyListOf(String.class))).thenReturn("Covid.1/1-0");

        formFieldPathGeneratorService.setFormNamespaceAndFieldPathForAddmoreObs(form2Observations, headerParts, asList(obs1,obs2));

        assertEquals("Covid.1/1-0", form2Observations.get(0).getFormFieldPath());
        assertEquals("Covid.1/1-1", form2Observations.get(1).getFormFieldPath());
        assertEquals("Bahmni", form2Observations.get(0).getFormNamespace());
        assertEquals("Bahmni", form2Observations.get(1).getFormNamespace());
    }
}
