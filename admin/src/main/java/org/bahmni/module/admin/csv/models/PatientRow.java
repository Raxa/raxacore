package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.module.admin.csv.utils.CSVUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PatientRow extends CSVEntity {
    @CSVHeader(name = "First Name")
    public String firstName;
    @CSVHeader(name = "Middle Name")
    public String middleName;
    @CSVHeader(name = "Last Name")
    public String lastName;
    @CSVHeader(name = "Registration Number")
    public String registrationNumber;
    @CSVHeader(name = "Gender")
    public String gender;
    @CSVHeader(name = "Age", optional = true)
    public String age;
    @CSVHeader(name = "Birth Date", optional = true)
    public String birthdate;
    @CSVHeader(name = "Registration Date", optional = true)
    public String registrationDate;

    @CSVRegexHeader(pattern = "Address.*")
    public List<KeyValue> addressParts;

    @CSVRegexHeader(pattern = "Attribute.*")
    public List<KeyValue> attributes;

    public Date getRegistrationDate() throws ParseException {
        if (registrationDate == null)
            return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(registrationDate);

    }
}
