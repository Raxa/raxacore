package org.bahmni.module.elisatomfeedclient.api.domain;

import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class OpenElisPatientTest {

    @Test
    public void shouldReturnBirthDateAsDate() throws Exception {
        OpenElisPatient openElisPatient = new OpenElisPatient();
        LocalDate today = LocalDate.now();
        openElisPatient.setDateOfBirth(today.toString("yyyy-MM-dd"));

        assertEquals(today.toDate(), openElisPatient.getDateOfBirthAsDate());
    }

}
