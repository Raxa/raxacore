package org.bahmni.module.admin.csv.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.KeyValue;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;

import java.util.List;

public class PatientRow extends CSVEntity {

    @CSVHeader(name = "First Name")
    private String firstName;
    @CSVHeader(name = "Middle Name")
    private String middleName;
    @CSVHeader(name = "Last Name")
    private String lastName;
    @CSVHeader(name = "Registration Number")
    private String registrationNumber;
    @CSVHeader(name = "Gender")
    private String gender;
    @CSVHeader(name = "Age")
    private String age;
    @CSVHeader(name = "Birth Date")
    private String birthdate;

    @CSVRegexHeader(pattern = "Address.*")
    private List<KeyValue> addressParts;

    @CSVRegexHeader(pattern = "Attribute.*")
    private List<KeyValue> attributes;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public List<KeyValue> getAddressParts() {
        return addressParts;
    }

    public void setAddressParts(List<KeyValue> addressParts) {
        this.addressParts = addressParts;
    }

    public List<KeyValue> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<KeyValue> attributes) {
        this.attributes = attributes;
    }
}
