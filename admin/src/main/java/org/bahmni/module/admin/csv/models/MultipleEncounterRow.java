package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.CSVHeader;
import org.bahmni.csv.CSVRegexHeader;
import org.bahmni.csv.CSVRepeatingRegexHeaders;
import org.bahmni.csv.KeyValue;

import java.util.List;

public class MultipleEncounterRow extends CSVEntity {

    @CSVHeader(name = "Registration Number")
    public String patientIdentifier;

    @CSVHeader(name = "encounterType")
    public String encounterType;

    @CSVHeader(name = "visitType")
    public String visitType;

    @CSVRegexHeader(pattern = "Patient.*")
    public List<KeyValue> patientAttributes;

    @CSVRepeatingRegexHeaders(type = EncounterRow.class)
    public List<EncounterRow> encounterRows;
}

