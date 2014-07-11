package org.bahmni.module.bahmnicore.mapper.builder;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;

import java.util.Date;

public class VisitBuilder {

    private final Visit visit;

    public VisitBuilder() {
        visit = new Visit();
    }

    public VisitBuilder withPerson(Person person) {
        visit.setPatient(new Patient(person));
        return this;
    }

    public VisitBuilder withUUID(String uuid) {
        visit.setUuid(uuid);
        return this;
    }

    public VisitBuilder withStartDatetime(Date startDatetime) {
        visit.setStartDatetime(startDatetime);
        return this;
    }

    public Visit build() {
        return visit;
    }
}
