package org.bahmni.module.bahmnicore.service;

import org.openmrs.Location;
import org.openmrs.Patient;
import java.util.Locale;

public interface SMSService {

    String getRegistrationMessage(Locale locale, Patient patient, Location location);

    Object sendSMS(String phoneNumber, String message,String reportingSessionCookie);
}