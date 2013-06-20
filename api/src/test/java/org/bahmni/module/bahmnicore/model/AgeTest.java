package org.bahmni.module.bahmnicore.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class AgeTest {
    @Test
    public void shouldCalculateAgeFromDateOfBirth() {
        Date birthDate = new LocalDate(1992, 12, 30).toDate();
        Date today = new LocalDate(2013, 6, 20).toDate();

        Age age = Age.fromDateOfBirth(birthDate, today);

        assertEquals(new Age(20, 5, 21), age);
    }

    @Test
    public void shouldCalculateDateOfBirthFromAge() {
        Age age = new Age(20, 5, 21);
        Date today = new LocalDate(2013, 6, 20).toDate();

        Date dateOfBirth = age.getDateOfBirth(today);

        assertEquals(new LocalDate(1992, 12, 30).toDate(), dateOfBirth);
    }
}
