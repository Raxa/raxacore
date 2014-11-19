package org.bahmni.module.admin.csv.models;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.csv.annotation.CSVRepeatingHeaders;
import org.bahmni.module.admin.csv.utils.CSVUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LabResultsRow extends CSVEntity {
    @CSVHeader(name = "Registration Number")
    private String patientIdentifier;

    @CSVRegexHeader(pattern = "Patient.*")
    private List<KeyValue> patientAttributes;

    @CSVHeader(name = "Date")
    private String testDateString;

    @CSVHeader(name = "Visit Type")
    private String visitType;

    @CSVRepeatingHeaders(names = {"Test", "Result"}, type = LabResultRow.class)
    private List<LabResultRow> testResults;

    public List<LabResultRow> getTestResults() {
        List<LabResultRow> labResultRows = new ArrayList<>();
        for (LabResultRow testResult : testResults) {
            if(!testResult.isEmpty()) {
                labResultRows.add(testResult);
            }
        }
        return labResultRows;
    }

    public void setTestResults(List<LabResultRow> testResults) {
        this.testResults = testResults;
    }

    public Date getTestDate() throws ParseException {
        return DateUtils.parseDate(testDateString, CSVUtils.ENCOUNTER_DATE_PATTERN);
    }

    public List<KeyValue> getPatientAttributes() {
        return patientAttributes;
    }

    public void setPatientAttributes(List<KeyValue> patientAttributes) {
        this.patientAttributes = patientAttributes;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public void setTestDateString(String testDateString) {
        this.testDateString = testDateString;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }
}

