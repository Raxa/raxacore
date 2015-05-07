package org.bahmni.module.bahmnicore.contract.patient.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
public class PatientResponse {

    private String uuid;
    private Date birthDate;
    private Date deathDate;
    private String identifier;
    private String addressFieldValue;
    private String givenName;
    private String middleName;
    private String familyName;
    private String gender;
    private Date dateCreated;
    private String activeVisitUuid;
    private String localName;

    public String getAge() {
        if (birthDate == null)
            return null;

        // Use default end date as today.
        Calendar today = Calendar.getInstance();

        // If date given is after date of death then use date of death as end date
        if (getDeathDate() != null && today.getTime().after(getDeathDate())) {
            today.setTime(getDeathDate());
        }

        Calendar bday = Calendar.getInstance();
        bday.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

        // Adjust age when today's date is before the person's birthday
        int todaysMonth = today.get(Calendar.MONTH);
        int bdayMonth = bday.get(Calendar.MONTH);
        int todaysDay = today.get(Calendar.DAY_OF_MONTH);
        int bdayDay = bday.get(Calendar.DAY_OF_MONTH);

        if (todaysMonth < bdayMonth) {
            age--;
        } else if (todaysMonth == bdayMonth && todaysDay < bdayDay) {
            // we're only comparing on month and day, not minutes, etc
            age--;
        }

        return Integer.toString(age);
    }

}
