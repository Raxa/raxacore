package org.bahmni.module.bahmnicore.service;

import org.openmrs.module.emrapi.patient.PatientProfile;
import org.springframework.transaction.annotation.Transactional;

public interface RegistrationSmsService {
    @Transactional(readOnly = true)
    Object sendRegistrationSMS(PatientProfile profile,String location,String reportingSessionCookie);
}
