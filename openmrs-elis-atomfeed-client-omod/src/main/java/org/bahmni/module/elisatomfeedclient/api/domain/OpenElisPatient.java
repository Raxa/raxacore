package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

@Data
public class OpenElisPatient {
    private String patientIdentifier;
    private String firstName;
    private String lastName;
    private String gender;
    private String address1;
    private String address2;
    private String address3;
    private String cityVillage;
    private String countyDistrict;
    private String stateProvince;
    private String dateOfBirth;
    private String healthCenter;

    private List<OpenElisPatientAttribute> attributes;

    public Date getDateOfBirthAsDate() {
        return  dateOfBirth == null ? null : DateTime.parse(dateOfBirth).toDate();
    }
}

