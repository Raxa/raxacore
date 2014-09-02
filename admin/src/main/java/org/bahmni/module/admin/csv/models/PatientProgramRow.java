package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.csv.KeyValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PatientProgramRow extends CSVEntity {

    public static final String ENROLLMENT_DATE_PATTERN = "d-M-yyyy";

    @CSVHeader(name = "Registration Number")
    public String patientIdentifier;

    @CSVRegexHeader(pattern = "Patient.*")
    public List<KeyValue> patientAttributes;

    @CSVHeader(name = "Program")
    public String programName;

    @CSVHeader(name = "EnrollmentDate")
    public String enrollmentDateTime;

    public Date getEnrollmentDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ENROLLMENT_DATE_PATTERN);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(enrollmentDateTime);
    }
}
