package org.bahmni.datamigration.request;

import java.util.ArrayList;
import java.util.List;

public class PatientRequest {
    private List<Names> names = new ArrayList<Names>();
    private Integer age;
    private String birthdate;
    private String gender;
    private String patientIdentifier;
    private String centerID;
    private List<PatientAddress> patientAddress = new ArrayList<PatientAddress>();
    private List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
}