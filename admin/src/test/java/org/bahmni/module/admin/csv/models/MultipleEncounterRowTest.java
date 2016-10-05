package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.KeyValue;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipleEncounterRowTest {
    @Test
    public void isEmptyReturnsTrueForEmptyRow() {
        Assert.isTrue(new MultipleEncounterRow().getNonEmptyEncounterRows().isEmpty(), "No data in encounter");

        MultipleEncounterRow emptyEncounterRow = new MultipleEncounterRowBuilder().getEmptyMultipleEncounterRow("GAN12345");
        Assert.isTrue(emptyEncounterRow.getNonEmptyEncounterRows().isEmpty(), "No data in encounter");
    }

    public MultipleEncounterRow getEmptyMultipleEncounterRow(String patientId) {
        List<KeyValue> emptyDiagnoses = new ArrayList<>();
        emptyDiagnoses.add(new KeyValue("diagnosis", " "));
        emptyDiagnoses.add(new KeyValue("diagnosis", " "));

        List<KeyValue> emptyObservations = new ArrayList<>();
        emptyObservations.add(new KeyValue("diagnosis", " "));
        emptyObservations.add(new KeyValue("diagnosis", " "));

        EncounterRow emptyEncounterRow = new EncounterRow();
        emptyEncounterRow.encounterDateTime = " ";
        emptyEncounterRow.obsRows = emptyObservations;
        emptyEncounterRow.diagnosesRows = emptyDiagnoses;

        MultipleEncounterRow multipleEncounterRow = new MultipleEncounterRow();
        multipleEncounterRow.patientIdentifier = patientId;
        multipleEncounterRow.encounterRows = Arrays.asList(emptyEncounterRow);
        return multipleEncounterRow;
    }

}