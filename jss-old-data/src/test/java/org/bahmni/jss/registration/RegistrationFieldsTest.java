package org.bahmni.jss.registration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegistrationFieldsTest {
    @Test
    public void parseDate() {
        assertEquals("05-08-1979", RegistrationFields.getDate("05/08/79 0:00"));
        assertEquals("05-08-1979", RegistrationFields.getDate("05/08/1979 00:00:00"));
    }

    @Test
    public void sentenceCase() {
        assertEquals("Devari", RegistrationFields.sentenceCase("DEVARI"));
        assertEquals("Chakra Kund", RegistrationFields.sentenceCase("CHAKRA KUND"));
    }
}