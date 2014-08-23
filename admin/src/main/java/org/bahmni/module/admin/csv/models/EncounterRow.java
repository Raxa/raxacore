package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.CSVHeader;
import org.bahmni.csv.CSVRegexHeader;
import org.bahmni.csv.KeyValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterRow extends CSVEntity {
    @CSVHeader(name = "registrationNumber")
    public String patientIdentifier;

    @CSVHeader(name = "encounterType")
    public String encounterType;

    @CSVHeader(name = "visitType")
    public String visitType;

    @CSVHeader(name = "Registration Date")
    public String encounterDateTime;

    @CSVRegexHeader(pattern = "Patient.*")
    public List<KeyValue> patientAttributes;

    @CSVRegexHeader(pattern = "Obs.*")
    public List<KeyValue> obsRows;

    @CSVRegexHeader(pattern = "Diagnosis.*")
    public List<KeyValue> diagnosesRows;

    public List<String> getDiagnoses() {
        List<String> aDiagnosesRows = new ArrayList<>();
        if (diagnosesRows != null) {
            for (KeyValue diagnosesRow : diagnosesRows) {
                aDiagnosesRows.add(diagnosesRow.getValue());
            }
        }
        return aDiagnosesRows;
    }

    public Date getEncounterDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(encounterDateTime);
    }

    public boolean hasObservations() {
        return obsRows != null && !obsRows.isEmpty();
    }
}
