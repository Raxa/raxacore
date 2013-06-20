package org.bahmni.module.bahmnicore.model;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;

public class Age {
    private final int years;
    private final int months;
    private final int days;

    public Age(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    public Date getDateOfBirth(Date forDate) {
        if (forDate == null) {
            forDate = new Date();
        }
        Period ageAsPeriod = new Period(years, months, 0, days, 0, 0, 0, 0, PeriodType.yearMonthDay());
        LocalDate dateOfBirth = new LocalDate(forDate).minus(ageAsPeriod);
        return dateOfBirth.toDate();
    }

    public static Age fromDateOfBirth(Date birthDate, Date today) {
        if (today == null) {
            today = new Date();
        }
        Period ageAsPeriod = new Period(new LocalDate(birthDate), new LocalDate(today), PeriodType.yearMonthDay());
        return new Age(ageAsPeriod.getYears(), ageAsPeriod.getMonths(), ageAsPeriod.getDays());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Age age = (Age) o;

        if (days != age.days) return false;
        if (months != age.months) return false;
        if (years != age.years) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = years;
        result = 31 * result + months;
        result = 31 * result + days;
        return result;
    }
}
