package org.bahmni.jss.registration;

import org.bahmni.datamigration.request.patient.Name;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegistrationFieldsTest {
    @Test
    public void parseDate() {
        assertEquals("05-08-1979", RegistrationFields.getDate("05/08/79 0:00"));
        assertEquals("05-08-1979", RegistrationFields.getDate("05/08/1979 00:00:00"));
        assertEquals("05-08-1979", RegistrationFields.getDate("05/08/1579 00:00:00"));
    }

    @Test
    public void sentenceCase() {
        assertEquals("Devari", RegistrationFields.sentenceCase("DEVARI"));
        assertEquals("Chakra Kund", RegistrationFields.sentenceCase("CHAKRA KUND"));
    }

    @Test
    public void name() {
        assertName("MILAPA BAI", "", "MILAPA", "BAI");
        assertName("MILAPA", "", "MILAPA", ".");
        assertName("MILAPA", "BAI", "MILAPA", "BAI");
        assertName("MILAPA JI", "BAI", "MILAPA JI", "BAI");
    }

    private void assertName(String firstName, String lastName, String givenName, String familyName) {
        Name name = RegistrationFields.name(firstName, lastName);
        assertEquals(givenName, name.getGivenName());
        assertEquals(familyName, name.getFamilyName());
    }

    @Test
    public void getAge() {
        assertEquals(1, RegistrationFields.getAge("1"));
        assertEquals(2, RegistrationFields.getAge("1.5"));
        assertEquals(0, RegistrationFields.getAge("10 Day"));
    }
}