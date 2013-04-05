package org.raxa.module.raxacore.web.v1_0.model;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person {

    private Date birthdate;
    private Integer age;
    private String centerName;
    private String patientIdentifier;
    private List<PersonAttribute> attributes = new ArrayList<PersonAttribute>();
    private List<Address> addresses = new ArrayList<Address>();
    private List<Name> names = new ArrayList<Name>();

    public org.openmrs.Person update(org.openmrs.Person person) {
        return null;
    }

    public Person(SimpleObject post) {
        SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);

        age = Integer.parseInt(extractor.<String>extract("age"));
        patientIdentifier = extractor.extract("patientIdentifier");
        SimpleObjectExtractor centerNameExtractor = new SimpleObjectExtractor(extractor.<SimpleObject>extract("centerID"));
        centerName = centerNameExtractor.extract("name");

        try {
            birthdate = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String>extract("birthdate"));
        } catch (Exception e) {
            //do something
        }

        List<SimpleObject> nameList = extractor.extract("names");
        for (SimpleObject name : nameList) {
            names.add(new Name(name));
        }

        List<SimpleObject> addressList = extractor.extract("addresses");
        for (SimpleObject address : addressList) {
            addresses.add(new Address(address));
        }

        List<SimpleObject> attributeList = extractor.extract("attributes");
        for (SimpleObject attribute : attributeList) {
            attributes.add(new PersonAttribute(attribute));
        }
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public Integer getAge() {
        return age;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public List<Name> getNames() {
        return names;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public String getCenterName() {
        return centerName;
    }

    public List<PersonAttribute> getAttributes() {
        return attributes;
    }
}
