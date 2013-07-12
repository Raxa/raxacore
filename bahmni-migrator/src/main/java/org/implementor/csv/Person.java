package org.implementor.csv;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.CSVHeader;

public class Person extends CSVEntity {
    @CSVHeader(name="Id")
    private String id;
    @CSVHeader(name="First Name")
    private String firstName;
    @CSVHeader(name="Last Name")
    private String lastName;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
